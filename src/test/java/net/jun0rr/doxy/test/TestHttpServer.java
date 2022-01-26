/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.file.Paths;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.http.HttpServerHandlerSetup;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpMessages;
import net.jun0rr.doxy.http.HttpServer;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestHttpServer {
  
  @Test
  public void method() throws InterruptedException {
    try {
      ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
      //SSLHandlerFactory factory = SSLHandlerFactory.forServer(Paths.get("d:/java/doxy.jks"), "32132155".toCharArray());
      SSLHandlerFactory factory = SSLHandlerFactory.forServer(Paths.get("/home/juno/java/doxy.jks"), "32132155".toCharArray());
      ChannelHandlerSetup<HttpHandler> setup = HttpServerHandlerSetup.newSetup()
          //.enableSSL(factory)
          .addConnectHandler(x->System.out.println("* Client connected: " + x.channel().remoteHost()))
          .addInputHandler(x->{
            long timeout = 10000;
            System.out.printf("* Waiting %d millis timeout...%n", timeout);
            Channel ch = x.channel().nettyChannel();
            x.session().global().<ScheduledThreadPoolExecutor>get("executor").get().schedule(()->{
              //System.out.printf("[SERVER] Sleeping for %d millis...%n", timeout);
              //Sleeper.of(timeout).sleep();
              System.out.println("[SERVER] Writing Last Content");
              ch.writeAndFlush(HttpMessages.lastContentUtf8(String.valueOf(timeout)))
                  .addListener(f->ch.close());
            }, timeout, TimeUnit.MILLISECONDS);
            return x.responseBuilder()
                .status(HttpResponseStatus.OK)
                .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                .addHeader(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.CHUNKED)
                .addHeader("x-chunked-timeout", timeout)
                //.addHeader(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(timeout).length())
                //.bodyUtf8(String.valueOf(timeout))
                .done()
                .sendAndFlush();
          })
          ;
      TcpChannel channel = HttpServer.open(setup)
          .bind(Host.of("0.0.0.0:4333"))
          .onComplete(c->System.out.println("[SERVER] listening on: " + c.channel().localAddress()))
          //.sync()
          .channel();
      channel.session().put("executor", ex);
      channel.events().awaitShutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
