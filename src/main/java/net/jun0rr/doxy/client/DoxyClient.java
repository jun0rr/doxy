/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.jun0rr.doxy.common.DoxyChannel;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketEncoder;
import net.jun0rr.doxy.common.ToNioBuffer;
import net.jun0rr.doxy.http.HttpClient;
import net.jun0rr.doxy.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRequest;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpExchange;
import net.jun0rr.doxy.tcp.TcpHandler;
import net.jun0rr.doxy.tcp.TcpChannelHandlerSetup;
import net.jun0rr.doxy.tcp.TcpServer;
import us.pserver.tools.Hash;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class DoxyClient {
  
  public static final String URI_PULL = "/pull";
  
  public static final String URI_PUSH = "/push";
  
  public static final String URI_RELEASE = "/release/%s";
  
  public static final String AUTH_FORMAT = "Bearer %s";
  
  
  private final TcpServer server;
  
  private final DoxyEnvironment env;
  
  private final PacketEncoder encoder;
  
  private final Map<String,TcpChannel> connections;
  
  private final ChannelHandlerSetup<HttpHandler> httpSetup;
  
  private final EventLoopGroup httpGroup;
  
  private final String jwt;
  
  private final PullService pullService;
  
  public DoxyClient(DoxyEnvironment env) {
    this.env = Objects.requireNonNull(env, "Bad null DoxyEnvironment");
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.httpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize(), env.executor());
    this.connections = new ConcurrentHashMap<>();
    this.httpSetup = HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addInputHandler(httpHandler());
    JwtClientFactory jcf = new JwtClientFactory(env);
    this.jwt = Unchecked.call(()->jcf.createAuthToken());
    ChannelHandlerSetup<TcpHandler> setup = TcpChannelHandlerSetup.newSetup()
        .addConnectHandler(connectHandler())
        .addInputHandler(encodeHandler())
        .addInputHandler(deliveryHandler());
    this.server = TcpServer.open(setup);
    this.pullService = new PullService(env, jwt);
  }
  
  public TcpServer startServer() {
    server.bind(env.configuration().getClientHost())
        .onComplete(c->System.out.println("[DOXYCLIENT] TcpServer listening on: " + c.channel().localHost()))
        .executeSync();
    server.closeFuture().onComplete(c->pullService.stop()).execute();
    return server;
  }
  
  private String channelID(TcpChannel ch) {
      return Hash.sha256().of(String.format("%s->%s", 
          ch.localHost(), 
          ch.remoteHost())
      );
  }
  
  private Supplier<HttpHandler> httpHandler() {
    return ()->x->{
      String con = x.response().headers().get(HttpHeaderNames.CONNECTION);
      if(con != null && HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(con)) {
        connections.remove(channelID(x.channel()));
        x.channel().events().close().execute();
      }
      return x.empty();
    };
  }
  
  private Supplier<Consumer<TcpExchange>> connectHandler() {
    return ()->c->{
      //DoxyChannel dc = DoxyChannel.of(env, channelID(c), c);
      //TcpChannel con = HttpClient.open(httpGroup, httpSetup)
          //.connect(env.configuration().getRemoteHost())
          //.channel();
      //env.channels().put(dc.uid(), dc);
      //connections.put(dc.uid(), con);
      //c.closeFuture().onComplete(d->{
        //env.channels().remove(dc.uid());
        //TcpChannel ch = getConnection(dc.uid());
        //connections.remove(dc.uid());
        //HttpRequest req = HttpRequest.of(
            //HttpVersion.HTTP_1_1, 
            //HttpMethod.GET, 
            //String.format(URI_RELEASE, dc.uid())
        //);
        //req.headers().set(HttpHeaderNames.AUTHORIZATION, String.format(AUTH_FORMAT, jwt));
        //ch.events().write(req).close().execute();
      //}).execute();
    };
  }
  
  private Supplier<TcpHandler> encodeHandler() {
    return ()->x->{
      if(x.message() != null && x.<ByteBuf>message().isReadable()) {
        DoxyChannel dc = env.channels().get(channelID(x.channel()));
        ByteBuffer data = ToNioBuffer.apply(x.message());
        Packet p = Packet.of(dc.uid(), data, env.configuration().getRemoteHost(), dc.nextOrder(), data.remaining(), false);
        return x.withMessage(encoder.encodePacket(p)).forward();
      }
      return x.empty();
    };
  }
  
  private Supplier<TcpHandler> deliveryHandler() {
    return ()->x->{
      Packet p = x.message();
      TcpChannel ch = getConnection(p.channelID());
      HttpRequest req = HttpRequest.of(
          HttpVersion.HTTP_1_1, 
          HttpMethod.POST, URI_PUSH, 
          Unpooled.wrappedBuffer(p.toByteBuffer())
      );
      req.headers().set(HttpHeaderNames.AUTHORIZATION, String.format(AUTH_FORMAT, jwt));
      ch.events().write(req).execute();
      return x.empty();
    };
  }
  
  private TcpChannel getConnection(String id) {
    TcpChannel ch = connections.get(id);
    if(ch == null) {
      ch = HttpClient.open(httpGroup, httpSetup)
          .connect(env.configuration().getRemoteHost())
          .channel();
      connections.put(id, ch);
    }
    return ch;
  }
  
}
