/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.client;

import cn.danielw.fop.ObjectPool;
import cn.danielw.fop.PoolConfig;
import cn.danielw.fop.Poolable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketCollection;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.common.PacketEncoder;
import net.jun0rr.doxy.common.ToNioBuffer;
import net.jun0rr.doxy.http.HttpClient;
import net.jun0rr.doxy.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.http.HttpExchange;
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
  
  
  private final DoxyEnvironment env;
  
  private TcpChannel server;
  
  private final PacketEncoder encoder;
  
  private final PacketDecoder decoder;
  
  private final Map<String,TcpChannel> tcpChannels;
  
  private final Map<String,Poolable<TcpChannel>> httpChannels;
  
  private final ChannelHandlerSetup<HttpHandler> httpSetup;
  
  private final EventLoopGroup httpGroup;
  
  private final String jwt;
  
  private final ObjectPool<TcpChannel> httpPool;
  
  private volatile boolean service;
  
  public DoxyClient(DoxyEnvironment env) {
    this.env = Objects.requireNonNull(env, "Bad null DoxyEnvironment");
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.decoder = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.httpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize());
    this.tcpChannels = new ConcurrentHashMap<>();
    this.httpChannels = new ConcurrentHashMap();
    JwtClientFactory jcf = new JwtClientFactory(env);
    this.jwt = Unchecked.call(()->jcf.createAuthToken());
    this.httpSetup = HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addOutputHandler(httpOutputJwtRequestHandler(jwt))
        .addInputHandler(httpNoContentHandler())
        .addInputHandler(httpDecodeHandler())
        .addInputHandler(httpResponseHandler())
        .addInputHandler(httpCloseHandler());
    this.service = true;
    PoolConfig cfg = new PoolConfig();
    cfg.setMinSize(1);
    cfg.setPartitionSize(env.configuration().getThreadPoolSize() / 2);
    cfg.setMaxSize(env.configuration().getThreadPoolSize());
    cfg.setMaxIdleMilliseconds((int)env.configuration().getTimeout());
    cfg.setMaxWaitMilliseconds((int)env.configuration().getTimeout());
    this.httpPool = new ObjectPool(cfg, new TcpChannelFactory(this::newHttpChannel));
  }
  
  public DoxyClient start() {
    ChannelHandlerSetup<TcpHandler> setup = TcpChannelHandlerSetup.newSetup()
        .addConnectHandler(tcpConnectHandler())
        .addInputHandler(tcpPacketHandler())
        .addInputHandler(tcpForwardHandler());
    this.server = TcpServer.open(setup, 1, env.configuration().getThreadPoolSize())
        .bind(env.configuration().getClientHost())
        .channel();
    httpGroup.scheduleAtFixedRate(httpPullService(), 0, 1, TimeUnit.SECONDS);
    return this;
  }
  
  public DoxyClient stop() {
    this.service = false;
    //httpChannels.values().forEach(c->c.getObject().events().close().execute());
    Unchecked.call(()->httpPool.shutdown());
    httpGroup.shutdownGracefully();
    tcpChannels.values().forEach(c->c.eventChain().close().execute());
    server.eventChain().shutdown().executeSync();
    return this;
  }
  
  public TcpChannel server() {
    return this.server;
  }
  
  public EventLoopGroup httpGroup() {
    return this.httpGroup;
  }
  
  private TcpChannel newHttpChannel() {
    Host target = env.configuration().getProxyConfig().getProxyHost() != null
            ? env.configuration().getProxyConfig().getProxyHost()
            : env.configuration().getServerHost();
    return HttpClient.open(httpGroup, httpSetup)
        .connect(target)
        .channel();
  }
  
  private Supplier<HttpHandler> httpOutputJwtRequestHandler(String jwt) {
    return ()->x->{
      x.request().headers().add(
          HttpHeaderNames.AUTHORIZATION, 
          String.format(AUTH_FORMAT, jwt)
      );
      return x.forward();
    };
  }
  
  private Supplier<HttpHandler> httpNoContentHandler() {
    return ()->x->{
      if(HttpResponseStatus.NO_CONTENT == x.response().status() 
          || x.response().message() == null 
          || !x.response().<ByteBuf>message().isReadable()) {
        disposeHttpChannel(x);
        return x.empty();
      }
      return x.forward();
    };
  }
  
  private Supplier<HttpHandler> httpDecodeHandler() {
    return ()->x->{
      Stream<Packet> s = PacketCollection.of(ToNioBuffer.apply(x.response().message()))
          .stream().map(p->decoder.decodePacket(p));
      return x.withResponseBody(s).forward();
    };
  }
  
  private Supplier<HttpHandler> httpResponseHandler() {
    return ()->x->{
      Stream<Packet> st = x.response().message();
      st.forEach(p->{
        TcpChannel ch = tcpChannels.get(p.channelID());
        if(ch != null) ch.eventChain()
            .write(Unpooled.wrappedBuffer(p.data()))
            .execute();
      });
      Poolable<TcpChannel> ch = httpPool.borrowObject(true);
      if(ch != null) {
        httpChannels.put(hash(ch.getObject()), ch);
        ch.getObject().eventChain().write(
            HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, URI_PULL))
            .execute();
      }
      return x.forward();
    };
  }
  
  private Supplier<HttpHandler> httpCloseHandler() {
    return ()-> x->{
      disposeHttpChannel(x);
      return x.empty();
    };
  }
  
  private void disposeHttpChannel(HttpExchange ex) {
    Poolable<TcpChannel> pch = httpChannels.remove(hash(ex.channel()));
    if(pch != null) httpPool.returnObject(pch);
    String conn = ex.response().headers().get(HttpHeaderNames.CONNECTION);
    if(conn != null && HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(conn)) {
      ex.channel().eventChain().close().execute();
    }
  }
  
  private Runnable httpPullService() {
    return ()->{
      if(service && httpChannels.isEmpty()) {
        Poolable<TcpChannel> ch = httpPool.borrowObject();
        if(ch != null) {
          httpChannels.put(hash(ch.getObject()), ch);
          ch.getObject().eventChain().write(
              HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, URI_PULL))
              .execute();
        }
      }
    };
  }
  
  private String hash(TcpChannel ch) {
    return Hash.sha256().of(String.format("%s->%s", ch.localHost(), ch.remoteHost()));
  }
  
  private Supplier<Consumer<TcpExchange>> tcpConnectHandler() {
    return ()->x->{
      String hash = hash(x.channel());
      tcpChannels.put(hash, x.channel());
      x.channel().closeFuture().onComplete(e->{
        tcpChannels.remove(hash);
        Poolable<TcpChannel> ch = httpPool.borrowObject();
        httpChannels.put(hash(ch.getObject()), ch);
        ch.getObject().eventChain()
            .write(HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, String.format(URI_RELEASE, hash)))
            .execute();
      });
    };
  }
  
  private Supplier<TcpHandler> tcpPacketHandler() {
    return ()->x->{
      ByteBuf b = x.message();
      Packet p = Packet.of(
          hash(x.channel()), 
          ToNioBuffer.apply(b), 
          env.configuration().getRemoteHost(), 
          0, b.readableBytes(), false
      );
      b.release(b.refCnt());
      return x.withMessage(p).forward();
    };
  }
  
  private Supplier<TcpHandler> tcpForwardHandler() {
    return ()->x->{
      Poolable<TcpChannel> ch = httpPool.borrowObject();
      httpChannels.put(hash(ch.getObject()), ch);
      ByteBuf msg = Unpooled.wrappedBuffer(encoder.encode(x.message()));
      ch.getObject().eventChain()
          .write(HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.POST, URI_PUSH, msg))
          .execute();
      return x.empty();
    };
  }
  
}
