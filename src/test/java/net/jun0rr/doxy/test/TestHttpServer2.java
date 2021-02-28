/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import io.netty.handler.codec.http.HttpHeaderValues;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.http.HttpClient;
import net.jun0rr.doxy.http.HttpClientHandlerSetup;
import net.jun0rr.doxy.http.HttpServerHandlerSetup;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRequest;
import net.jun0rr.doxy.http.HttpRoute;
import net.jun0rr.doxy.http.HttpServer;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestHttpServer2 {
  
  private static final CountDownLatch count = new CountDownLatch(2);
  
  private TcpChannel server() {
    //SSLHandlerFactory factory = SSLHandlerFactory.forServer(Paths.get("d:/java/doxy.jks"), "32132155".toCharArray());
    SSLHandlerFactory factory = SSLHandlerFactory.forServer(Paths.get("/home/juno/java/doxy.jks"), "32132155".toCharArray());
    ChannelHandlerSetup<HttpHandler> setup = HttpServerHandlerSetup.newSetup()
        .addOutputHandler(()->x->{
          System.out.println("[SERVER] HttpHandler (R2) >>> " + x.response().message());
          x.response().headers().forEach(e->System.out.printf("   - %s: %s%n", e.getKey(), e.getValue()));
          return x.forward();
        })
        .addOutputHandler(()->x->{
          System.out.println("[SERVER] HttpHandler (R1) >>> " + x.response().message());
          return x.forward();
        })
        .addRouteHandler(HttpRoute.any("/.*", HttpMethod.GET), ()->x->{
          x.response().headers().add("x-routeA", "/.*");
          return x.sendAndClose();
        })
        .addRouteHandler(HttpRoute.any("/echo.*", HttpMethod.POST), ()->x->{
          x.response().headers().set(CONNECTION, CLOSE);
          x.response().headers().add("x-routeB", "/echo.*");
          return x.withResponse(x.response().withMessage(x.request().message())).sendAndClose();
        })
        .enableSSL(factory)
        .addInputHandler(()->x->{
          System.out.printf("[SERVER] HttpHandler (F1)");
          StringBuilder req = new StringBuilder(">> ");
          req.append(x.request().method())
              .append(" ")
              .append(x.request().uri())
              .append(" ")
              .append(x.request().protocolVersion())
              .append("\n");
          x.request().headers().forEach(e->req.append(String.format("  %s: %s%n", e.getKey(), e.getValue())));
          x.setAttr("reqstr", req);
          return x.forward();
        })
        .addInputHandler(()->x->{
          System.out.println("[SERVER] HttpHandler (F2) >>> " + x.request().message());
          StringBuilder req = x.<StringBuilder>getAttr("reqstr").get();
          if(x.request().message() != null && x.request().<ByteBuf>message().isReadable()) {
            String msg = x.request().<ByteBuf>message().toString(StandardCharsets.UTF_8);
            req.append("  message: ").append(msg).append("\n");
            System.out.println(req.toString());
            return x.withRequest(x.request().withMessage(msg)).forward();
          }
          System.out.println(req.toString());
          return x.forward();
        })
        .addInputHandler(()->x->{
          System.out.println("[SERVER] HttpHandler (H1) >>> " + x.request().message());
          x.response().headers().set("x-doxy-custom", "subliminar message");
          return x.forward();
        });
    return HttpServer.open(setup)
        .bind(Host.of("0.0.0.0:4321"))
        .onComplete(c->System.out.println("[SERVER] listening on: " + c.channel().localHost()))
        .executeSync()
        .channel();
  }
  
  private TcpChannel getRoot() {
    ChannelHandlerSetup<HttpHandler> setup = HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addInputHandler(()->x->{
          StringBuilder res = new StringBuilder("---- GET / ----\n");
          res.append(x.response().status())
              .append(" ")
              .append(x.response().protocolVersion()).append("\n");
          x.response().headers().forEach(e->res.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
          if(x.response().message() != null && x.response().<ByteBuf>message().isReadable()) {
            ByteBuf msg = x.response().message();
            res.append("  message: ").append(msg.toString(StandardCharsets.UTF_8)).append("\n");
          }
          System.out.println(res);
          count.countDown();
          x.channel().eventChain()
              .close()
              .onComplete(c->
                  x.bootstrapChannel().eventChain().shutdown().execute())
              .execute();
          return x.empty();
        });
    HttpRequest req = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    req.headers().set(HttpHeaderNames.ACCEPT, HttpHeaderValues.TEXT_PLAIN)
        .set(HttpHeaderNames.HOST, "https://localhost:4321");
    return HttpClient.open(setup)
        .connect(Host.of("localhost:4321"))
        .write(req)
        .execute()
        .channel();
  }
  
  private TcpChannel postEcho() {
    ChannelHandlerSetup<HttpHandler> setup = HttpClientHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addInputHandler(()->x->{
          StringBuilder res = new StringBuilder("---- POST /echo ----\n");
          res.append(x.response().status())
              .append(" ")
              .append(x.response().protocolVersion()).append("\n");
          x.response().headers().forEach(e->res.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
          if(x.response().message() != null && x.response().<ByteBuf>message().isReadable()) {
            ByteBuf msg = x.response().message();
            res.append("  message: ").append(msg.toString(StandardCharsets.UTF_8)).append("\n");
          }
          System.out.println(res);
          count.countDown();
          x.channel().eventChain().shutdown().execute();
          return x.empty();
        });
    HttpRequest req = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.POST, "/echo", "Hello World Netty (post /echo)");
    req.headers().set(HttpHeaderNames.ACCEPT, HttpHeaderValues.TEXT_PLAIN)
        .set(HttpHeaderNames.HOST, "https://localhost:4321");
    return HttpClient.open(setup)
        .connect(Host.of("localhost:4321"))
        .write(req)
        .execute()
        .channel();
  }
  
  @Test
  public void method() throws InterruptedException {
    try {
      TcpChannel server = server();
      TcpChannel getroot = getRoot();
      TcpChannel postecho = postEcho();
      count.await();
      server.eventChain().shutdown().onComplete(g->System.out.println("[SERVER] Shutdown complete")).executeSync();
      getroot.eventChain().awaitShutdown().onComplete(g->System.out.println("[GET /] Shutdown complete")).executeSync();
      postecho.eventChain().awaitShutdown().onComplete(g->System.out.println("[POST /echo] Shutdown complete")).executeSync();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
