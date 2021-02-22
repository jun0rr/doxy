/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.client;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static net.jun0rr.doxy.client.DoxyClient.AUTH_FORMAT;
import static net.jun0rr.doxy.client.DoxyClient.URI_PULL;
import net.jun0rr.doxy.common.AbstractRunnable;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.PacketCollection;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.server.http.HttpClient;
import net.jun0rr.doxy.server.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.server.http.HttpHandler;
import net.jun0rr.doxy.server.http.HttpRequest;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.common.ToNioBuffer;


/**
 *
 * @author Juno
 */
public class PullService extends AbstractRunnable {
  
  private final PacketDecoder dec;
  
  private final AtomicBoolean close;
  
  private final String jwt;
  
  public PullService(DoxyEnvironment env, String jwt) {
    super(env);
    this.dec = new PacketDecoder(env.configuration().getSecurityConfig().getCryptAlgorithm(), env.getPrivateKey());
    this.close = new AtomicBoolean(false);
    this.jwt = Objects.requireNonNull(jwt, "Bad null Json Web Token String");
  }
  
  @Override
  public void run() {
    ChannelHandlerSetup<HttpHandler> setup = new HttpClientHandlerSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addInputHandler(decodeHandler())
        .addInputHandler(deliveryHandler());
    TcpChannel cli = HttpClient.open(setup)
        .connect(env.configuration().getRemoteHost())
        .channel();
    while(isRunning() && !env.channels().isEmpty()) {
      if(close.compareAndSet(true, false)) {
        cli = HttpClient.open(setup)
            .connect(env.configuration().getRemoteHost())
            .channel();
      }
      HttpRequest req = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, URI_PULL);
      req.headers().set(HttpHeaderNames.AUTHORIZATION, String.format(AUTH_FORMAT, jwt));
      cli.events().write(req).execute();
    }
    this.running = false;
  }
  
  private Supplier<HttpHandler> decodeHandler() {
    return ()->x->{
      if(HttpResponseStatus.OK == x.response().status() 
          && x.response().message() != null 
          && x.response().<ByteBuf>message().isReadable()) {
        Stream<Packet> s = PacketCollection.of(ToNioBuffer.apply(x.response().message()))
            .stream().map(p->dec.decodePacket(p));
        return x.withMessage(s).forward();
      }
      return x.empty();
    };
  }
  
  private Supplier<HttpHandler> deliveryHandler() {
    return ()->x->{
      Stream<Packet> st = x.response().message();
      st.forEach(p->env.getChannel(p.channelID())
          .ifPresent(c->c.channel().events().write(p.data()).execute()));
      if(x.response().headers().contains(HttpHeaderNames.CONNECTION)
          && HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(
              x.response().headers().get(HttpHeaderNames.CONNECTION))) {
        close.compareAndSet(false, true);
        x.channel().events().shutdown().execute();
      }
      return x.empty();
    };
  }
  
}
