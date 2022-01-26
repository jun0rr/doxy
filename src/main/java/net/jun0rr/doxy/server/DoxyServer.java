/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.jun0rr.doxy.common.DoxyChannel;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.common.PacketEncoder;
import net.jun0rr.doxy.common.ToNioBuffer;
import net.jun0rr.doxy.http.HttpExceptionalResponse;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpMessages;
import net.jun0rr.doxy.http.HttpRoute;
import net.jun0rr.doxy.http.HttpServer;
import net.jun0rr.doxy.http.HttpServerHandlerSetup;
import net.jun0rr.doxy.http.util.RequestParam;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpClient;
import net.jun0rr.doxy.tcp.TcpHandler;
import net.jun0rr.doxy.tcp.TcpChannelHandlerSetup;
import net.jun0rr.doxy.tcp.TcpEvents;
import net.jun0rr.doxy.tcp.TcpExchange;
import us.pserver.tools.Hash;


/**
 *
 * @author Juno
 */
public class DoxyServer {
  
  public static final String ATTR_OUT_ENABLED = "output-enabled";
  
  public static final String URI_CHANNEL_ID = "channelId";
  
  public static final String URI_RELEASE = String.format("/release/{%s}", URI_CHANNEL_ID);
  
  private final DoxyEnvironment env;
  
  private final PacketEncoder encoder;
  
  private final PacketDecoder decoder;
  
  private TcpChannel server;
  
  private final EventLoopGroup tcpGroup;
  
  private final BlockingQueue<Packet> outputPackets;
  
  private volatile TcpChannel pushChannel;
  
  private volatile TcpChannel pullChannel;
  
  public DoxyServer(DoxyEnvironment env) {
    this.env = env;
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.decoder = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.tcpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize());
    this.outputPackets = new LinkedBlockingQueue();
  }
  
  public TcpChannel start() {
    SSLHandlerFactory factory = SSLHandlerFactory.forServer(
        env.configuration().getSecurityConfig().getKeystorePath(), 
        env.configuration().getSecurityConfig().getKeystorePassword()
    );
    ChannelHandlerSetup<HttpHandler> setup = HttpServerHandlerSetup.newSetup()
        .enableSSL(factory)
        .addInputHandler(new JwtAuthFilter(env))
        .addInputHandler(this::httpContentHandler)
        .addRouteHandler(HttpRoute.post("/push"), this::httpPushHandler)
        .addRouteHandler(HttpRoute.get("/pull"), this::httpPullHandler)
        .addRouteHandler(HttpRoute.get("/release/\\w+"), this::httpReleaseHandler)
        .addRouteHandler(HttpRoute.get("/shutdown"), this::httpShutdownHandler);
    server = HttpServer.open(setup, 1, env.configuration().getThreadPoolSize())
        .bind(env.configuration().getServerHost())
        .onComplete(c->System.out.println("[HTTP] HttpServer listening on: " + c.channel().localAddress()))
        .sync()
        .channel();
    return server;
  }
  
  private void sendPacket(Packet p) {
    Optional<DoxyChannel> opt = server.session()
        .valuesOf(DoxyChannel.class)
        .filter(c->p.channelID().equals(c.uid()))
        .findAny();
    TcpEvents events;
    if(opt.isEmpty()) {
      ChannelHandlerSetup<TcpHandler> setup = TcpChannelHandlerSetup.newSetup()
          .addInputHandler(this::tcpResponseHandler);
      events = TcpClient.open(tcpGroup, setup).connect(p.remote());
      events.onComplete(e->server.session().put(hash(events.channel()), DoxyChannel.of(p.channelID(), events.channel())))
          .onComplete(e->System.out.println("[TCP] Connected to " + e.channel().remoteAddress()));
    }
    else {
      events = opt.get().channel().events();
    }
    events.writeAndFlush(Unpooled.wrappedBuffer(p.data()))
        .onComplete(e->System.out.printf("[TCP] Message Sent: %s (%d bytes), channelID=%s%n", e.channel().remoteAddress(), p.originalLength(), p.channelID()));
  }
  
  private Optional<? extends TcpExchange> tcpResponseHandler(TcpExchange x) {
    ByteBuffer data = ToNioBuffer.apply(x.message(), env::alloc, true);
    DoxyChannel ch = server.session().<DoxyChannel>get(hash(x.channel())).get();
    System.out.printf("[TCP] Response Received %s - %s (%d bytes) - pullChannel=%s%n", ch.channel().remoteHost(), ch.uid(), data.remaining(), pullChannel);
    Packet p = Packet.of(ch.uid(), data, ch.channel().remoteHost(), 0, data.remaining(), false);
    pullChannel.events()
        .writeAndFlush(HttpMessages.content(Unpooled.wrappedBuffer(p.toByteBuffer())))
        .onComplete(e->System.out.println("[HTTP] Response sent"));
    return x.empty();
  }
  
  private Optional<HttpExchange> httpPullHandler(HttpExchange x) {
    System.out.println("PULL HANDLER isHttpMessage: " + x.isHttpMessage());
    if(x.isHttpMessage()) {
      pullChannel = x.channel();
      System.out.println("PULL CHANNEL INITIALIZED: " + pullChannel);
      return x.responseBuilder()
          .ok()
          .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM)
          .addHeader(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.IDENTITY)
          .addHeader(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED)
          .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
          .done()
          .sendAndFlush();
    }
    return x.empty();
  }
  
  private Optional<HttpExchange> httpPushHandler(HttpExchange x) {
    System.out.println("PUSH HANDLER isHttpMessage: " + x.isHttpMessage());
    if(x.isHttpMessage()) {
      pushChannel = x.channel();
    }
    return x.empty();
  }
  
  private Optional<HttpExchange> httpContentHandler(HttpExchange x) {
    if(x.isHttpMessage()) return x.forward();
    if(x.isHttpContent()) {
      Packet p = Packet.of(ToNioBuffer.apply(x.<HttpContent>message(), env::alloc));
      byte[] bs = new byte[p.data().remaining()];
      p.data().get(bs);
      p.data().flip();
      System.out.printf("[HTTP] Packet received: %s -> %s (%d bytes), md5=%s%n", p.remote(), p.channelID(), p.originalLength(), Hash.md5().of(bs));
      sendPacket(p);
    }
    return x.empty();
  }
  
  private Optional<HttpExchange> httpShutdownHandler(HttpExchange x) {
    x.channel().closeFuture()
        .shutdown()
        .onComplete(e->server.session().valuesOf(DoxyChannel.class).forEach(DoxyChannel::close))
        .onComplete(e->tcpGroup.shutdownGracefully())
        .onComplete(c->x.bootstrapChannel().events().shutdown())
        .onComplete(c->System.out.println("[HTTP] Shutdown completed"));
    return x.responseBuilder()
        .noContet()
        .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
        .done()
        .sendAndClose();
  }
  
  private Optional<HttpExchange> httpReleaseHandler(HttpExchange x) {
    RequestParam par = RequestParam.fromUriPattern(URI_RELEASE, x.request().uri());
    if(!par.containsValid(URI_CHANNEL_ID)) {
      return x.response(HttpExceptionalResponse.of(
          HttpResponseStatus.BAD_REQUEST, 
          new IllegalArgumentException(String.format("Missing %s in URI %s", URI_CHANNEL_ID, URI_RELEASE))
      )).sendAndClose();
    }
    server.session().entriesOf(DoxyChannel.class)
        .filter(e->par.get(URI_CHANNEL_ID).toString().equals(e.getValue().uid()))
        .findAny()
        .ifPresent(e->{
          server.session().remove(e.getKey());
          e.getValue().close();
        });
    return x.responseBuilder()
        .noContet()
        .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
        .done()
        .sendAndClose();
  }
  
  private String hash(TcpChannel ch) {
    return Hash.md5().of(String.format("%s->%s", ch.localHost(), ch.remoteHost()));
  }
  
}
