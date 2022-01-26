/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.http.HttpClient;
import net.jun0rr.doxy.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestHttpClient {
  /*
  @Test
  public void method() throws InterruptedException {
    ChannelHandlerSetup<HttpHandler> setup = HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addInputHandler(()->x->{
          StringBuilder res = new StringBuilder("---- GET google.com ----\n");
          boolean close = true;
          if(x.isHttpMessage()) {
            res.append(x.response().status())
                .append(" ")
                .append(x.response().protocolVersion()).append("\n");
            x.response().headers().forEach(e->res.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
            if(x.response().headers().contains(HttpHeaderNames.CONNECTION)) {
              close = x.response().headers().get(HttpHeaderNames.CONNECTION).equals(HttpHeaderValues.CLOSE);
            }
          }
          if(x.isHttpContent()) {
            ByteBuf msg = x.<HttpContent>message().content();
            res.append("  message: ").append(msg.toString(StandardCharsets.UTF_8)).append("\n");
          }
          if(x.isLastHttpContent()) {
            x.channel().eventChain()
                .shutdown()
                .onComplete(c->System.out.println("[CLIENT] Shutdown complete!"))
                .execute();
          }
          System.out.println(res);
          return x.empty();
        });
    HttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    req.headers().set(HttpHeaderNames.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
    HttpClient.open(setup)
        .connect(Host.of("google.com:443"))
        .onComplete(c->System.out.println("[CLIENT] Connected to " + c.channel().remoteHost()))
        .write(req)
        .onComplete(c->System.out.println("[CLIENT] Request writed!"))
        .executeSync()
        .awaitShutdown();
  }
  */
}
