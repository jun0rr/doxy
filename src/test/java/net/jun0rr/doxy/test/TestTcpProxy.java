/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.http.HttpRoute;
import net.jun0rr.doxy.http.HttpServer;
import net.jun0rr.doxy.http.HttpServerHandlerSetup;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpChannelHandlerSetup;
import net.jun0rr.doxy.tcp.TcpClient;
import net.jun0rr.doxy.tcp.TcpServer;
import org.junit.jupiter.api.Test;

/**
 *
 * @author juno
 */
public class TestTcpProxy {
  
  //@Test
  public TcpChannel httpServer() throws URISyntaxException {
    Path kspath = Paths.get(getClass().getClassLoader().getResource("doxy.jks").toURI());
    SSLHandlerFactory ssl = SSLHandlerFactory.forServer(kspath, "32132155".toCharArray());
    ChannelHandlerSetup setup = HttpServerHandlerSetup.newSetup()
        .enableSSL(ssl)
        .addConnectHandler(()-> x->{
          System.out.println("[HTTP] Client connected: " + x.channel().remoteHost());
          x.forward();
        })
        .addInputHandler(()-> x->{
          if(x.request().message() != null 
              && x.request().message() instanceof ByteBuf) {
            return x.withRequest(x.request()
                .withMessage(x.request().<ByteBuf>message()
                    .toString(StandardCharsets.UTF_8)))
                .forward();
          }
          return x.forward();
        })
        .addInputHandler(()-> x->{
          StringBuilder sb = new StringBuilder("<h2>");
          sb.append(x.request().method()).append(" ")
              .append(x.request().uri()).append("</h2>")
              .append("<h4>Headers:</h4>")
              .append("<ul style='font-family: monospace; font-size: 14px;'>");
          x.request().headers().entries().forEach(e->sb.append("<li>")
              .append(e.getKey())
              .append(": ")
              .append(e.getValue())
              .append("</li>")
          );
          sb.append("</ul>");
          if(x.request().message() != null && !x.request().<String>message().trim().isEmpty()) {
            sb.append("<h4>Message:</h4>")
                .append("<div style='width: 500px; height: 80px; padding: 10px 20px 10px 20px; border: 1px solid green; border-radius: 3px; font-family: monospace; font-size: 14px;'>")
                .append(x.request().<String>message())
                .append("</div>");
          }
          x.setAttr("resp", sb);
          return x.forward();
        })
        .addRouteHandler(HttpRoute.of("/.*"), ()-> x->
            x.withResponse(x.response().withMessage(
                x.getAttr("resp").get().toString())
            ).sendAndClose())
        .addRouteHandler(HttpRoute.of("/shutdown.*"), ()-> x->{
          System.out.println("[HTTP] Shutting Down...");
          x.channel().events()
              .write(x.response().withMessage(x.getAttr("resp").get().toString()))
              .close()
              .onComplete(e->{
                x.bootstrapChannel().events()
                  .shutdown()
                  .onComplete(f->System.out.println("[HTTP] Shutdown Completed!"))
                  .execute();
                e.future();
              })
              .execute();
          return x.empty();
        })
        .addOutputHandler(()-> x->{
          x.response().headers().set("x-output-instant", Instant.now().toString());
          return x.forward();
        })
        ;
    return HttpServer.open(setup)
        .bind(Host.of("localhost", 4443))
        .onComplete(e->System.out.println("[HTTP] Listening on " + e.channel().localHost()))
        .executeSync()
        .channel();
  }
  
  
  public TcpChannel proxy() {
    Map<Host,TcpChannel> conns = new ConcurrentHashMap();
    ChannelHandlerSetup setup = TcpChannelHandlerSetup.newSetup()
        .addConnectHandler(()->x->{
          TcpChannel client = x.channel();
          System.out.println("[PROXY] Client Connected " + client.remoteHost());
          ChannelHandlerSetup targetSetup = TcpChannelHandlerSetup.newSetup()
              .addInputHandler(()->y->{
                System.out.println("[TARGET] Message Received " + y.message());
                client.events().write(y.message()).execute();
                return y.empty();
              });
          conns.put(client.remoteHost(), TcpClient.open(targetSetup)
              .connect(Host.of("localhost", 4443)).channel()
          );
          x.channel().closeFuture().onComplete(e->
              conns.remove(client.remoteHost())
                  .events().shutdown().execute()
          );
        })
        .addInputHandler(()->x->{
          System.out.println("[PROXY] Client Message << " + x.channel().remoteHost());
          conns.get(x.channel().remoteHost())
              .events()
              .write(x.message())
              .execute();
          return x.empty();
        });
    return TcpServer.open(setup)
        .bind(Host.of("localhost", 4444))
        .onComplete(e->System.out.println("[PROXY] Listening on " + e.channel().localHost()))
        .executeSync()
        .channel();
  }
  
  @Test
  public void test() throws URISyntaxException {
    TcpChannel http = httpServer();
    TcpChannel proxy = proxy();
    http.events().awaitShutdown();
    proxy.events()
        .shutdown()
        .onComplete(e->System.out.println("[PROXY] Shutdown Completed!"))
        .executeSync();
  }
  
}
