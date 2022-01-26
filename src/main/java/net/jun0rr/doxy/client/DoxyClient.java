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
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.common.PacketEncoder;
import net.jun0rr.doxy.common.ToNioBuffer;
import net.jun0rr.doxy.http.HttpClient;
import net.jun0rr.doxy.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpMessages;
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
  
  public static final byte[] CONNECT_BYTES = "CONNECT".getBytes(StandardCharsets.UTF_8);
  
  
  private final DoxyEnvironment env;
  
  private TcpChannel server;
  
  private final PacketEncoder encoder;
  
  private final PacketDecoder decoder;
  
  private final EventLoopGroup httpGroup;
  
  private final String jwt;
  
  private volatile TcpChannel pushChannel;
  
  private volatile TcpChannel pullChannel;
  
  private volatile boolean service;
  
  public DoxyClient(DoxyEnvironment env) {
    this.env = Objects.requireNonNull(env, "Bad null DoxyEnvironment");
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.decoder = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.httpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize());
    JwtClientFactory jcf = new JwtClientFactory(env);
    this.jwt = Unchecked.call(()->jcf.createAuthToken());
    this.service = true;
  }
  
  private ChannelHandlerSetup<HttpHandler> httpSetup() {
    return HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addOutputHandler(this::httpJwtOutputHandler)
        .addInputHandler(this::httpNoContentFilter)
        .addInputHandler(this::httpContentHandler);
  }
  
  private TcpChannel createChannel() {
    Host target = env.configuration().getProxyConfig().getProxyHost() != null
            ? env.configuration().getProxyConfig().getProxyHost()
            : env.configuration().getServerHost();
    return HttpClient.open(httpGroup, httpSetup())
        .connect(target)
        .channel();
  }
  
  private TcpChannel createPushChannel() {
    HttpRequest req = HttpMessages.request()
        .post(URI_PUSH)
        .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM)
        .addHeader(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.IDENTITY)
        .addHeader(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED)
        .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
        .build();
    pushChannel = createChannel().events()
        .onComplete(e->System.out.println("[HTTP PUSH] Connected at " + e.channel().remoteAddress()))
        .write(req)
        .onComplete(e->System.out.println("[HTTP PUSH] Request Sent!"))
        .sync()
        .closeFuture()
        .onComplete(e->{
          System.out.println("[HTTP PUSH] Closing...");
          if(service) createPushChannel();
        })
        .channel();
    return pushChannel;
  }
  
  private TcpChannel createPullChannel() {
    HttpRequest req = HttpMessages.request()
        .get(URI_PULL)
        .addHeader(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_OCTET_STREAM)
        .addHeader(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.IDENTITY)
        .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
        .build();
    pullChannel = createChannel().events()
        .onComplete(e->System.out.println("[HTTP PULL] Connected at " + e.channel().remoteAddress()))
        .write(req)
        .write(LastHttpContent.EMPTY_LAST_CONTENT)
        .onComplete(e->System.out.println("[HTTP PULL] Request Sent!"))
        .sync()
        .channel()
        .closeFuture()
        .onComplete(e->{
          System.out.println("[HTTP PULL] Closing...");
          if(service) createPullChannel();
        })
        .channel();
    return pullChannel;
  }
  
  public TcpChannel start() {
    ChannelHandlerSetup<TcpHandler> setup = TcpChannelHandlerSetup.newSetup()
        .addConnectHandler(this::tcpConnectHandler)
        .addInputHandler(this::tcpConnectRequest)
        .addInputHandler(this::tcpInputHandler);
    this.server = TcpServer.open(setup, 1, env.configuration().getThreadPoolSize())
        .bind(env.configuration().getClientHost())
        .onComplete(e->System.out.println("[TCP] Server listening on " + e.channel().localAddress()))
        .channel();
    return server;
  }
  
  public DoxyClient stop() {
    service = false;
    httpGroup.shutdownGracefully();
    server.events().shutdown().sync();
    return this;
  }
  
  public TcpChannel tcpServer() {
    return this.server;
  }
  
  public EventLoopGroup httpGroup() {
    return this.httpGroup;
  }
  
  private void releaseChannel(String id) {
    server.session().remove(id);
    HttpRequest req = HttpMessages.request()
        .get(String.format(URI_RELEASE, id))
        .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
        .build();
    createChannel().events()
        .write(req)
        .onComplete(e->System.out.println("[HTTP RELEASE] Channel: " + id))
        .close();
  }
  
  private void tcpConnectHandler(TcpExchange x) {
    System.out.println("[TCP] Client Connected: " + x.channel().remoteHost());
    String id = hash(x.channel());
    server.session().put(id, x.channel());
    x.channel().closeFuture().onComplete(f->releaseChannel(id));
    if(pushChannel == null || !pushChannel.nettyChannel().isOpen()) {
      createPushChannel();
    }
    if(pullChannel == null || !pullChannel.nettyChannel().isOpen()) {
      createPullChannel();
    }
  }
  
  private Optional<? extends TcpExchange> tcpConnectRequest(TcpExchange x) {
    System.out.println("[DoxyClient.tcpConnectRequest] message=" + x.message());
    if(x.message() == null) return x.empty();
    int ridx = x.<ByteBuf>message().readerIndex();
    int widx = x.<ByteBuf>message().writerIndex();
    byte[] start = new byte[CONNECT_BYTES.length];
    x.<ByteBuf>message().readBytes(start);
    x.<ByteBuf>message().readerIndex(ridx);
    x.<ByteBuf>message().writerIndex(widx);
    if(Arrays.equals(CONNECT_BYTES, start)) {
      return x.message(Unpooled.copiedBuffer("HTTP/1.1 200 Connection established\r\n\r\n", StandardCharsets.UTF_8))
          .sendAndFlush();
    }
    return x.forward();
  }
  
  private Optional<? extends TcpExchange> tcpInputHandler(TcpExchange x) {
    //ByteBuffer data = ToNioBuffer.apply(x.message(), env::alloc, true);
    byte[] bs = new byte[x.<ByteBuf>message().readableBytes()];
    x.<ByteBuf>message().readBytes(bs);
    Packet p = Packet.of(hash(x.channel()), ByteBuffer.wrap(bs), env.configuration().getRemoteHost(), 0, bs.length, false);
    System.out.printf("[TCP] Message Received: %s (%d bytes), channelID=%s, md5=%s%n", x.channel().remoteHost(), bs.length, p.channelID(), Hash.md5().of(bs));
    ByteBuf buf = Unpooled.wrappedBuffer(p.toByteBuffer());
    String msg = String.format("[HTTP PUSH] Request Sent: %d bytes", buf.readableBytes());
    pushChannel.events()
        .write(HttpMessages.content(buf))
        .onComplete(e->System.out.println(msg));
    return x.empty();
  }
  
  private Optional<HttpExchange> httpNoContentFilter(HttpExchange x) {
    return x.isHttpMessage() 
        && HttpResponseStatus.NO_CONTENT == x.response().status()
        ? x.empty() : x.forward();
  }
  
  private Optional<HttpExchange> httpContentHandler(HttpExchange x) {
    if(x.isHttpMessage()) printResponse(x.response());
    if(x.isHttpContent() && x.<HttpContent>message().content().isReadable()) {
      Packet p = Packet.of(ToNioBuffer.apply(x.<HttpContent>message(), env::alloc));
      System.out.println("[HTTP PULL] Packet Received: " + p.channelID());
      x.session().<TcpChannel>get(p.channelID())
          .ifPresent(c->c.events()
              .writeAndFlush(Unpooled.wrappedBuffer(p.data()))
              .onComplete(e->System.out.println("[TCP] Client Response writed: " + p.originalLength())));
    }
    return x.empty();
  }
  
  private Optional<HttpExchange> httpJwtOutputHandler(HttpExchange x) {
    if(x.isHttpMessage()) {
      x.request().headers().add(
          HttpHeaderNames.AUTHORIZATION, 
          String.format(AUTH_FORMAT, jwt)
      );
      x.request().headers().add(
          HttpHeaderNames.HOST, x.channel().remoteHost().getHostname()
      );
      if(env.configuration().getUserAgent() != null) {
        x.request().headers().add(
            HttpHeaderNames.USER_AGENT, env.configuration().getUserAgent()
        );
      }
    }
    return x.forward();
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
    return Hash.md5().of(String.format("%s->%s", ch.localHost(), ch.remoteHost()));
  }
  
}
