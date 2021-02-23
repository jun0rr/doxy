/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import net.jun0rr.doxy.common.DoxyChannel;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketCollection;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.common.PacketEncoder;
import net.jun0rr.doxy.common.ToNioBuffer;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRoute;
import net.jun0rr.doxy.http.HttpServer;
import net.jun0rr.doxy.http.HttpServerHandlerSetup;
import net.jun0rr.doxy.http.util.RequestParam;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpClient;
import net.jun0rr.doxy.tcp.TcpHandler;
import net.jun0rr.doxy.tcp.TcpChannelHandlerSetup;


/**
 *
 * @author Juno
 */
public class DoxyServer {
  
  private final DoxyEnvironment env;
  
  private final PacketEncoder encoder;
  
  private final PacketDecoder decoder;
  
  private final HttpServer server;
  
  private final EventLoopGroup tcpGroup;
  
  private final Map<String,AtomicLong> stats;
  
  public DoxyServer(DoxyEnvironment env) {
    this.env = env;
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.decoder = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.tcpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize(), env.executor());
    this.stats = new ConcurrentHashMap<>();
    SSLHandlerFactory factory = SSLHandlerFactory.forServer(
        env.configuration().getSecurityConfig().getKeystorePath(), 
        env.configuration().getSecurityConfig().getKeystorePassword()
    );
    ChannelHandlerSetup<HttpHandler> setup = HttpServerHandlerSetup.newSetup()
        .addRouteHandler(HttpRoute.get("/pull"), pull())
        .addRouteHandler(HttpRoute.post("/push"), push())
        .addRouteHandler(HttpRoute.get("/release/\\w+"), release())
        .addRouteHandler(HttpRoute.get("/shutdown"), shutdown())
        .addRouteHandler(HttpRoute.get("/stats"), ()->new DoxyStatsHandler(stats))
        .enableSSL(factory)
        .addInputHandler(()->new JwtAuthFilter(env));
    this.server = HttpServer.open(new NioEventLoopGroup(2), new NioEventLoopGroup(env.configuration().getThreadPoolSize()), setup);
  }
  
  private Supplier<HttpHandler> push() {
    return ()->x->{
      if(x.request().message() != null && x.request().<ByteBuf>message().isReadable()) {
        Packet p = Packet.of(ToNioBuffer.apply(x.request().message()));
        DoxyChannel dc = getChannel(p);
        getCount(String.format("%s.requestCount", dc.channel().remoteHost()))
            .incrementAndGet();
        getCount(String.format("%s.requestBytes", dc.channel().remoteHost()))
            .addAndGet(p.originalLength());
        getChannel(p).writePacket(decoder.decodePacket(p));
      }
      return x.empty();
    };
  }
  
  private Supplier<HttpHandler> pull() {
    return ()->x->{
      Collection<Packet> pcs = getOutbox();
      if(!pcs.isEmpty()) {
        PacketCollection pc = PacketCollection.of(pcs);
        return x.withMessage(Unpooled.wrappedBuffer(pc.toByteBuffer())).send();
      }
      x.response().setStatus(HttpResponseStatus.NO_CONTENT);
      return x.send();
    };
  }
  
  private Supplier<HttpHandler> shutdown() {
    return ()->x->{
      x.channel().events()
          .shutdown()
          .onComplete(c->x.bootstrapChannel().events().shutdown().execute())
          .execute();
      return x.empty();
    };
  }
  
  private Collection<Packet> getOutbox() throws InterruptedException {
    List<Packet> pcs = new LinkedList<>();
    Packet p = env.outbox().pollFirst(env.configuration().getServerTimeout(), TimeUnit.MILLISECONDS);
    while(p != null) {
      pcs.add(p);
      p = env.outbox().pollFirst();
    }
    return pcs;
  }
  
  private Supplier<HttpHandler> release() {
    return ()->x->{
      RequestParam par = RequestParam.fromUriPattern("/release/{channelId}", x.request().uri());
      DoxyChannel dc = env.channels().remove(par.get("channelId").toString());
      if(dc != null) {
        dc.channel().events().close().execute();
      }
      return x.sendAndClose();
    };
  }
  
  private DoxyChannel getChannel(Packet p) {
    Optional<DoxyChannel> opt = env.getChannel(p.channelID());
    if(opt.isEmpty()) {
      getCount(String.format("%s.startup", p.remote()))
          .set(System.currentTimeMillis());
    }
    return opt.orElse(DoxyChannel.of(env, p.channelID(), TcpClient.open(tcpGroup, 
          TcpChannelHandlerSetup.newSetup().addInputHandler(tcpHandler(p.channelID()))
      ).connect(p.remote()).channel()));
  }
  
  public AtomicLong getCount(String key) {
    AtomicLong count = stats.get(key);
    if(count == null) {
      count = new AtomicLong(0L);
      stats.put(key, count);
    }
    return count;
  }
  
  private Supplier<TcpHandler> tcpHandler(String cid) {
    return ()->x->{
      if(x.message() != null && x.<ByteBuf>message().isReadable()) {
        ByteBuffer msg = ToNioBuffer.apply(x.message());
        DoxyChannel dc = env.channels().get(cid);
        Packet p = Packet.of(cid, msg, x.channel().remoteHost(), dc.nextOrder(), msg.remaining(), false);
        getCount(String.format("%s.responseCount", x.channel().remoteHost()))
            .incrementAndGet();
        getCount(String.format("%s.responseBytes", x.channel().remoteHost()))
            .addAndGet(p.originalLength());
        env.outbox().offer(encoder.encodePacket(p));
      }
      return x.empty();
    };
  }
  
  public HttpServer startServer() {
    getCount("startup").set(System.currentTimeMillis());
    server.bind(env.configuration().getServerHost())
        .onComplete(c->System.out.println("[DOXYSERVER] HttpServer listening on: " + c.channel().localHost()))
        .executeSync();
    return server;
  }
  
}
