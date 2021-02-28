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
import java.util.function.Supplier;
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
import net.jun0rr.doxy.tcp.EventChain;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpClient;
import net.jun0rr.doxy.tcp.TcpHandler;
import net.jun0rr.doxy.tcp.TcpChannelHandlerSetup;
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
  
  private final Map<String,DoxyChannel> channels;
  
  public DoxyServer(DoxyEnvironment env) {
    this.env = env;
    this.encoder = new PacketEncoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPublicKey());
    this.decoder = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.tcpGroup = new NioEventLoopGroup(env.configuration().getThreadPoolSize());
    this.outputPackets = new LinkedBlockingQueue();
    this.channels = new ConcurrentHashMap();
  }
  
  public TcpChannel start() {
    SSLHandlerFactory factory = SSLHandlerFactory.forServer(
        env.configuration().getSecurityConfig().getKeystorePath(), 
        env.configuration().getSecurityConfig().getKeystorePassword()
    );
    ChannelHandlerSetup<HttpHandler> setup = HttpServerHandlerSetup.newSetup()
        .enableSSL(factory)
        .addOutputHandler(this::httpOutputPacketsHandler)
        .addInputHandler(()->new JwtAuthFilter(env))
        .addRouteHandler(HttpRoute.post("/push"), this::httpPushHandler)
        .addRouteHandler(HttpRoute.get("/pull"), this::httpPullHandler)
        .addRouteHandler(HttpRoute.get("/release/\\w+"), this::httpReleaseHandler)
        .addRouteHandler(HttpRoute.get("/shutdown"), this::httpShutdownHandler);
    server = HttpServer.open(setup, 1, env.configuration().getThreadPoolSize())
        .bind(env.configuration().getServerHost())
        .onComplete(c->System.out.println("[HTTP] HttpServer listening on: " + c.channel().localHost()))
        .executeSync()
        .channel();
    return server;
  }
  
  private void sendPacket(Packet p) {
    Optional<DoxyChannel> opt = channels.values().stream()
        .filter(c->p.channelID().equals(c.uid())).findAny();
    EventChain chain;
    if(opt.isEmpty()) {
      ChannelHandlerSetup<TcpHandler> setup = TcpChannelHandlerSetup.newSetup()
          .addInputHandler(this::tcpResponseHandler);
      chain = TcpClient.open(tcpGroup, setup)
          .connect(p.remote())
          .onComplete(e->channels.put(hash(e.channel()), DoxyChannel.of(p.channelID(), e.channel())))
          .onComplete(e->System.out.println("[TCP] Connected to " + e.channel().remoteHost()));
    }
    else {
      chain = opt.get().channel().eventChain();
    }
    chain.write(Unpooled.wrappedBuffer(p.data()))
        .onComplete(e->System.out.printf("[TCP] Message Sent: %s - %s%n", e.channel().remoteHost(), p.channelID()))
        .execute();
  }
  
  private TcpHandler tcpResponseHandler() {
    return x->{
      ByteBuffer data = ToNioBuffer.apply(x.message(), env::alloc, true);
      DoxyChannel ch = channels.get(hash(x.channel()));
      if(ch == null) {
        x.channel().eventChain().close().execute();
      }
      System.out.printf("[TCP] Response Received %s - %s (%d bytes)%n", ch.channel().remoteHost(), ch.uid(), data.remaining());
      Packet p = Packet.of(ch.uid(), data, ch.channel().remoteHost(), 0, data.remaining(), false);
      //outputPackets.offer(encoder.encodePacket(p));
      outputPackets.offer(p);
      return x.empty();
    };
  }
  
  private HttpHandler httpOutputPacketsHandler() {
    return x->{
      List<Packet> ls = new LinkedList();
      ls.add(outputPackets.poll(1000, TimeUnit.MILLISECONDS));
      outputPackets.stream().forEach(ls::add);
      PacketCollection pks = PacketCollection.of(ls);
      pks.stream()
          .forEach(p->System.out.printf("[HTTP] Output delivery: %s -> %s (%d bytes)%n", p.remote(), p.channelID(), p.originalLength()));
      HttpResponse res;
      if(!pks.isEmpty()) {
        ByteBuf msg = Unpooled.wrappedBuffer(pks.toByteBuffer(env::alloc));
        res = HttpResponse.of(HttpResponseStatus.OK, msg);
        res.headers()
            .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM)
            .set(HttpHeaderNames.CONTENT_DISPOSITION, HttpHeaderValues.ATTACHMENT);
      }
      else {
        res = HttpResponse.of(HttpResponseStatus.NO_CONTENT);
      }
      res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
      return x.withResponse(res).forward();
    };
  }
  
  private HttpHandler httpPushHandler() {
    return x->{
      Packet p = Packet.of(ToNioBuffer.apply(x.request().message(), env::alloc, true));
      System.out.printf("[HTTP] Packet received: %s -> %s (%d bytes)%n", p.remote(), p.channelID(), p.originalLength());
      //sendPacket(decoder.decodePacket(p));
      sendPacket(p);
      return x.sendAndClose();
    };
  }
  
  private HttpHandler httpPullHandler() {
    return x->x.sendAndClose();
  }
  
  private HttpHandler httpShutdownHandler() {
    return x->{
      x.channel().closeFuture()
          .shutdown()
          .onComplete(e->channels.values().forEach(DoxyChannel::close))
          .onComplete(e->channels.clear())
          .onComplete(e->tcpGroup.shutdownGracefully())
          .onComplete(c->x.bootstrapChannel().eventChain().shutdown().execute())
          .onComplete(c->System.out.println("[HTTP] Shutdown completed"))
          .execute();
      return x.sendAndClose();
    };
  }
  
  private HttpHandler httpReleaseHandler() {
    return x->{
      RequestParam par = RequestParam.fromUriPattern(URI_RELEASE, x.request().uri());
      if(!par.containsValid(URI_CHANNEL_ID)) {
        return x.withResponse(HttpExceptionalResponse.of(
            HttpResponseStatus.BAD_REQUEST, 
            new IllegalArgumentException(String.format("Missing %s in URI %s", URI_CHANNEL_ID, URI_RELEASE))
        )).sendAndClose();
      }
      Optional<Map.Entry<String,DoxyChannel>> ch = channels.entrySet().stream()
          .filter(e->par.get(URI_CHANNEL_ID).toString().equals(e.getValue().uid()))
          .findAny();
      if(ch.isPresent()) {
        channels.remove(ch.get().getKey());
        ch.get().getValue().channel().eventChain().close().execute();
      }
      return x.sendAndClose();
    };
  }
  
  private String hash(TcpChannel ch) {
    return Hash.sha256().of(String.format("%s->%s", ch.localHost(), ch.remoteHost()));
  }
  
}
