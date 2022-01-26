/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.http.HttpClient;
import net.jun0rr.doxy.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpMessages;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestHttpClientStandalone {
  
  /*
  public Runnable runnable(String message) {
    return ()->{
      ChannelHandlerSetup<HttpHandler> setup = HttpClientHandlerSetup.newSetup()
          .enableSSL(SSLHandlerFactory.forClient())
          .addInputHandler(()->x->{
            System.out.printf("[HTTP] isHttpMessage=%s, isHttpContent=%s, isLastHttpContent=%s, isEmptyLastContent=%s%n", x.isHttpMessage(), x.isHttpContent(), x.isLastHttpContent(), x.isEmptyLastContent());
            StringBuilder res = new StringBuilder();
            if(x.isHttpMessage()) {
              res.append(String.format("[%s - %s]%n", x.response().status(), x.response().protocolVersion()));
              x.response().headers().forEach(e->res.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
            }
            if(x.isHttpContent() && !x.isEmptyLastContent()) {
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
      HttpRequest req = HttpMessages.request()
          .get("/echo")
          .addQueryParam("message", message)
          .addHeader(HttpHeaderNames.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
          .build();
      System.out.println(req.uri());
      HttpClient.open(setup)
          .connect(Host.of("localhost:4321"))
          .onComplete(c->System.out.println("[CLIENT] Connected to " + c.channel().remoteHost()))
          .write(req)
          .onComplete(c->System.out.println("[CLIENT] Request writed!"))
          .executeSync()
          .awaitShutdown();
    };
  }
  
  @Test
  public void runRequests() throws InterruptedException {
    Thread[] ts = new Thread[50];
    for(int i =0; i < ts.length; i++) {
      ts[i] = new Thread(runnable(String.format("Hello from Thread-%d", i)), String.format("thread-%d", i));
    }
    for(int i =0; i < ts.length; i++) {
      ts[i].start();
    }
    for(int i =0; i < ts.length; i++) {
      ts[i].join();
    }
  }
  */
}
