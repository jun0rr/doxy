/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server;
/*
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.common.DoxyChannel;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketCollection;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.common.PacketEncoder;
import net.jun0rr.doxy.common.ToNioBuffer;
import net.jun0rr.doxy.http.HttpExceptionalResponse;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRequest;
import net.jun0rr.doxy.http.HttpResponse;
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
import us.pserver.tools.Hash;


/**
 *
 * @author Juno
 *
public class DoxyServer1 {
  
  public static final String ATTR_OUT_ENABLED = "output-enabled";
  
  public static final String URI_CHANNEL_ID = "channelId";
  
  public static final String URI_RELEASE = String.format("/release/{%s}", URI_CHANNEL_ID);
  
  private final DoxyEnvironment env;
  
  private final PacketEncoder encoder;
  
  private final PacketDecoder decoder;
  
  private final HttpServer server;
  
  private final EventLoopGroup tcpGroup;
  
  private final Map<String,AtomicLong> stats;
  
  private final BlockingQueue<Packet> outputPackets;
  
  private final Map<String,DoxyChannel> channels;
  
  private final IntFunction<ByteBuffer> alloc;
  
  private final ChannelHandlerSetup<TcpHandler> tcpSetup;
  
  public DoxyServer1(DoxyEnvironment env) {
    this.env = env;
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.decoder = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.tcpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize());
    this.stats = new ConcurrentHashMap<>();
    this.outputPackets = new LinkedBlockingQueue();
    this.channels = new ConcurrentHashMap();
    this.alloc = i->env.configuration().isDirectBufferEnabled()
        ? ByteBuffer.allocateDirect(i)
        : ByteBuffer.allocate(i);
    this.tcpSetup = TcpChannelHandlerSetup.newSetup()
        .addInputHandler(tcpRemoteResponseHandler());
    SSLHandlerFactory factory = SSLHandlerFactory.forServer(
        env.configuration().getSecurityConfig().getKeystorePath(), 
        env.configuration().getSecurityConfig().getKeystorePassword()
    );
    ChannelHandlerSetup<HttpHandler> setup = HttpServerHandlerSetup.newSetup()
        .enableSSL(factory)
        .addOutputHandler(httpOutputPacketsHandler())
        .addInputHandler(()->new JwtAuthFilter(env))
        .addRouteHandler(HttpRoute.get("/pull"), httpPullHandler())
        .addRouteHandler(HttpRoute.post("/push"), httpPushHandler())
        .addRouteHandler(HttpRoute.get("/release/\\w+"), httpReleaseHandler())
        .addRouteHandler(HttpRoute.get("/shutdown"), httpShutdownHandler())
        .addRouteHandler(HttpRoute.get("/stats"), ()->new DoxyStatsHandler(stats));
    this.server = HttpServer.open(setup, 1, env.configuration().getThreadPoolSize());
  }
  
  private TcpChannel newTcpChannel(Host target) {
    return TcpClient.open(tcpGroup, tcpSetup)
        .connect(target)
        .onComplete(e->System.out.println("[TCP] Connected to " + e.channel().remoteHost()))
        .execute()
        .channel();
  }
  
  private DoxyChannel getChannel(Packet p) {
    Optional<DoxyChannel> ch = channels.values().stream()
        .filter(c->p.channelID().equals(c.uid())).findAny();
    if(ch.isEmpty()) {
      ch = Optional.of(DoxyChannel.of(p.channelID(), newTcpChannel(p.remote())));
      channels.put(hash(ch.get().channel()), ch.get());
    }
    return ch.get();
  }
  
  private Supplier<HttpHandler> httpOutputPacketsHandler() {
    return ()-> x->{
      Optional<Boolean> outEnabled = x.getAttr(ATTR_OUT_ENABLED);
      System.out.printf("[HTTP] outboundPackets.outEnabled.isPresent=%s, outEnabled=%s, outputPackets.size=%d%n", outEnabled.isPresent(), outEnabled.orElse(false), outputPackets.size());
      printRequest(x.request());
      if(!outEnabled.isPresent() || !outEnabled.get()) return x.forward();
      List<Packet> ls = new LinkedList();
      ls.add(outputPackets.poll(env.configuration().getTimeout(), TimeUnit.MILLISECONDS));
      outputPackets.stream()
          .map(p->encoder.encodePacket(p))
          .peek(p->System.out.printf("[HTTP] Output delivery: %s -> %s (%d bytes)%n", p.remote(), p.channelID(), p.originalLength()))
          .forEach(ls::add);
      PacketCollection pkts = PacketCollection.of(ls);
      HttpResponse res;
      if(!pkts.isEmpty()) {
        ByteBuf msg = Unpooled.wrappedBuffer(pkts.toByteBuffer(this.alloc));
        getCount(String.format("%s.deliveryCount", x.channel().remoteHost()))
            .addAndGet(pkts.size());
        getCount(String.format("%s.deliveryBytes", x.channel().remoteHost()))
            .addAndGet(msg.readableBytes());
        res = HttpResponse.of(HttpResponseStatus.OK, msg);
        res.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM)
            .set(HttpHeaderNames.CONTENT_DISPOSITION, HttpHeaderValues.ATTACHMENT)
            .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      }
      else {
        res = HttpResponse.of(HttpResponseStatus.NO_CONTENT);
      }
      return x.response(res).forward();
    };
  }
  
  private Supplier<HttpHandler> httpPushHandler() {
    return ()->x->{
      if(x.request().message() != null && x.request().<ByteBuf>message().isReadable()) {
        Packet p = Packet.of(ToNioBuffer.apply(x.request().message()));
        System.out.printf("[HTTP] Packet received: %s -> %s (%d bytes)%n", p.remote(), p.channelID(), p.originalLength());
        DoxyChannel dc = getChannel(p);
        getCount(String.format("%s.requestCount", dc.channel().remoteHost()))
            .incrementAndGet();
        getCount(String.format("%s.requestBytes", dc.channel().remoteHost()))
            .addAndGet(p.originalLength());
        dc.channel().eventChain()
            .write(decoder.decodePacket(p))
            .onComplete(e->System.out.printf("[TCP] Packet writed: remote=%s, bytes=%d, channel=%s%n", e.channel().remoteHost(), p.originalLength(), p.channelID()))
            .executeSync();
        //dc.writePacketData(decoder.decodePacket(p));
      }
      return x.setAttr(ATTR_OUT_ENABLED, true).forward();
    };
  }
  
  private Supplier<HttpHandler> httpPullHandler() {
    return ()->x->x.setAttr(ATTR_OUT_ENABLED, true).forward();
  }
  
  private Supplier<HttpHandler> httpShutdownHandler() {
    return ()->x->{
      System.out.println("[HTTP] Shutdown requested");
      x.channel().closeFuture()
          .shutdown()
          .onComplete(e->channels.values().forEach(DoxyChannel::close))
          .onComplete(e->channels.clear())
          .onComplete(e->outputPackets.clear())
          .onComplete(e->tcpGroup.shutdownGracefully())
          .onComplete(c->x.bootstrapChannel().eventChain().shutdown().execute())
          .onComplete(c->System.out.println("[HTTP] Shutdown completed"))
          .execute();
      return x.setAttr(ATTR_OUT_ENABLED, false)
          .response(HttpResponse.of(HttpResponseStatus.OK))
          .sendAndClose();
    };
  }
  
  private Supplier<HttpHandler> httpReleaseHandler() {
    return ()->x->{
      System.out.println("[HTTP] Release: " + x.request().uri());
      RequestParam par = RequestParam.fromUriPattern(URI_RELEASE, x.request().uri());
      if(!par.containsValid(URI_CHANNEL_ID)) {
        return x.withResponse(HttpExceptionalResponse.of(
            HttpResponseStatus.BAD_REQUEST, 
            new IllegalArgumentException(String.format("Missing %s in URI %s", URI_CHANNEL_ID, URI_RELEASE))
        )).send();
      }
      Optional<Map.Entry<String,DoxyChannel>> ch = channels.entrySet().stream()
          .filter(e->par.get(URI_CHANNEL_ID).toString().equals(e.getValue().uid()))
          .findAny();
      if(ch.isPresent()) {
        channels.remove(ch.get().getKey());
        ch.get().getValue().channel().eventChain().close().execute();
      }
      return x.setAttr(ATTR_OUT_ENABLED, true).send();
    };
  }
  
  public AtomicLong getCount(String key) {
    AtomicLong count = stats.get(key);
    if(count == null) {
      count = new AtomicLong(0L);
      stats.put(key, count);
    }
    return count;
  }
  
  private Supplier<TcpHandler> tcpRemoteResponseHandler() {
    return ()->x->{
      if(x.message() != null && x.<ByteBuf>message().isReadable()) {
        ByteBuf buf = x.<ByteBuf>message();
        System.out.println("[TCP] Response received: " + buf.readableBytes());
        ByteBuffer msg = ToNioBuffer.apply(buf, this.alloc, true);
        DoxyChannel ch = channels.get(hash(x.channel()));
        if(ch == null) {
          x.channel().eventChain().close().execute();
        }
        else {
          getCount(String.format("%s.responseCount", x.channel().remoteHost()))
              .incrementAndGet();
          getCount(String.format("%s.responseBytes", x.channel().remoteHost()))
              .addAndGet(msg.remaining());
          outputPackets.offer(Packet.of(ch.uid(), msg, ch.channel().remoteHost(), 0, msg.remaining(), false));
        }
      }
      return x.empty();
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
  
  public HttpServer startServer() {
    getCount("startup").set(System.currentTimeMillis());
    server.bind(env.configuration().getServerHost())
        .onComplete(c->System.out.println("[HTTP] HttpServer listening on: " + c.channel().localHost()))
        .executeSync();
    return server;
  }
  
}
*/