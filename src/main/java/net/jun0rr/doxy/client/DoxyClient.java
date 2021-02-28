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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketCollection;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.common.PacketEncoder;
import net.jun0rr.doxy.common.ToNioBuffer;
import net.jun0rr.doxy.http.HttpClient;
import net.jun0rr.doxy.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRequest;
import net.jun0rr.doxy.http.HttpResponse;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpHandler;
import net.jun0rr.doxy.tcp.TcpChannelHandlerSetup;
import net.jun0rr.doxy.tcp.TcpExchange;
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
  
  
  private final DoxyEnvironment env;
  
  private TcpChannel server;
  
  private final PacketEncoder encoder;
  
  private final PacketDecoder decoder;
  
  private final Map<String,TcpChannel> tcpChannels;
  
  private final EventLoopGroup httpGroup;
  
  private final String jwt;
  
  private final AtomicInteger httpCount;
  
  private volatile boolean service;
  
  public DoxyClient(DoxyEnvironment env) {
    this.env = Objects.requireNonNull(env, "Bad null DoxyEnvironment");
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.decoder = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.httpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize());
    this.tcpChannels = new ConcurrentHashMap<>();
    JwtClientFactory jcf = new JwtClientFactory(env);
    this.jwt = Unchecked.call(()->jcf.createAuthToken());
    this.httpCount = new AtomicInteger(0);
    this.service = true;
  }
  
  private ChannelHandlerSetup<HttpHandler> httpSetup() {
    return HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addOutputHandler(httpJwtRequestHandler(jwt))
        .addInputHandler(this::httpResponseHandler)
        .addInputHandler(this::httpCloseHandler);
  }
  
  private void sendHttpRequest(HttpRequest req) {
    Host target = env.configuration().getProxyConfig().getProxyHost() != null
            ? env.configuration().getProxyConfig().getProxyHost()
            : env.configuration().getServerHost();
    httpCount.incrementAndGet();
    HttpClient.open(httpGroup, httpSetup())
        .connect(target)
        .onComplete(e->System.out.println("[HTTP] Connected at " + e.channel().remoteHost()))
        .write(req)
        .onComplete(e->System.out.println("[HTTP] Request Sent: " + req.uri()))
        .execute();
  }
  
  public TcpChannel start() {
    ChannelHandlerSetup<TcpHandler> setup = TcpChannelHandlerSetup.newSetup()
        .addConnectHandler(this::tcpConnectHandler)
        .addInputHandler(this::tcpInputHandler);
    this.server = TcpServer.open(setup, 1, env.configuration().getThreadPoolSize())
        .bind(env.configuration().getClientHost())
        .onComplete(e->System.out.println("[TCP] Server listening on " + e.channel().localHost()))
        .execute()
        .channel();
    return server;
  }
  
  public DoxyClient stop() {
    service = false;
    httpGroup.shutdownGracefully();
    tcpChannels.values().forEach(c->c.eventChain().close().execute());
    server.eventChain().shutdown().executeSync();
    return this;
  }
  
  public TcpChannel tcpServer() {
    return this.server;
  }
  
  public EventLoopGroup httpGroup() {
    return this.httpGroup;
  }
  
  private Consumer<TcpExchange> tcpConnectHandler() {
    return x->{
      System.out.println("[TCP] Client Connected: " + x.channel().remoteHost());
      tcpChannels.put(hash(x.channel()), x.channel());
    };
  }
  
  private TcpHandler tcpInputHandler() {
    return x->{
      //System.out.println("[TCP] Message Received: " + x.message());
      ByteBuffer data = ToNioBuffer.apply(x.message(), env::alloc, true);
      Packet p = Packet.of(hash(x.channel()), data, env.configuration().getRemoteHost(), 0, data.remaining(), false);
      System.out.printf("[TCP] Message Received: %s - %s%n", x.channel().remoteHost(), p.channelID());
      //HttpRequest req = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.POST, URI_PUSH, Unpooled.wrappedBuffer(encoder.encode(p)));
      HttpRequest req = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.POST, URI_PUSH, Unpooled.wrappedBuffer(p.toByteBuffer()));
      sendHttpRequest(req);
      return x.empty();
    };
  }
  
  private HttpHandler httpResponseHandler() {
    return x->{
      printResponse(x.response());
      ByteBuf buf = x.response().message() != null
          ? x.response().message()
          : Unpooled.EMPTY_BUFFER;
      System.out.println("[HTTP] Response Received: " + buf.readableBytes());
      if(HttpResponseStatus.OK == x.response().status() && buf.isReadable()) {
        PacketCollection pks = PacketCollection.of(ToNioBuffer.apply(buf, env::alloc, true));
        pks.stream()
            .peek(p->System.out.println("[HTTP] Packet Received: " + p.channelID()))
            .filter(p->tcpChannels.containsKey(p.channelID()))
            //.map(decoder::decodePacket)
            .forEach(p->tcpChannels.get(p.channelID())
                .eventChain()
                .write(Unpooled.wrappedBuffer(p.data()))
                .onComplete(e->System.out.println("[TCP] Client Response writed: " + p.originalLength()))
                .execute()
            );
      }
      return x.forward();
    };
  }
  
  private HttpHandler httpCloseHandler() {
    return x->{
      x.channel().eventChain().close().execute();
      if(httpCount.decrementAndGet() < 1 && service) {
        sendHttpRequest(HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, URI_PULL));
      }
      return x.empty();
    };
  }
  
  private Supplier<HttpHandler> httpJwtRequestHandler(String jwt) {
    return ()->x->{
      x.request().headers().add(
          HttpHeaderNames.AUTHORIZATION, 
          String.format(AUTH_FORMAT, jwt)
      );
      return x.forward();
    };
  }
  
  private void printResponse(HttpResponse res) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(res.status()).append("]\n");
    res.headers().forEach(e->sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
    System.out.println(sb);
  }
  
  private void printRequest(HttpRequest req) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(req.method()).append(" ").append(req.uri()).append("]\n");
    req.headers().forEach(e->sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
    System.out.println(sb);
  }
  
  private String hash(TcpChannel ch) {
    return Hash.sha256().of(String.format("%s->%s", ch.localHost(), ch.remoteHost()));
  }
  
}
