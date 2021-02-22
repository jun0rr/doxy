/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.server.http.HttpClient;
import net.jun0rr.doxy.server.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.server.http.HttpHandler;
import net.jun0rr.doxy.server.http.HttpRequest;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestHttpClient {
  
  @Test
  public void method() throws InterruptedException {
    ChannelHandlerSetup<HttpHandler> setup = HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addInputHandler(()->x->{
          StringBuilder res = new StringBuilder("---- GET google.com ----\n");
          res.append(x.response().status())
              .append(" ")
              .append(x.response().protocolVersion()).append("\n");
          x.response().headers().forEach(e->res.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
          if(x.response().message() != null && x.response().<ByteBuf>message().isReadable()) {
            ByteBuf msg = x.response().message();
            res.append("  message: ").append(msg.toString(StandardCharsets.UTF_8)).append("\n");
          }
          System.out.println(res);
          x.channel().events()
              .shutdown()
              .onComplete(c->System.out.println("[CLIENT] Shutdown complete!"))
              .execute();
          return x.empty();
        });
    HttpRequest req = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    req.headers().set(HttpHeaderNames.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
    HttpClient.open(setup)
        .connect(Host.of("google.com:443"))
        .onComplete(c->System.out.println("[CLIENT] Connected to " + c.channel().remoteHost()))
        .write(req)
        .onComplete(c->System.out.println("[CLIENT] Request writed!"))
        .executeSync()
        .awaitShutdown();
  }
}
