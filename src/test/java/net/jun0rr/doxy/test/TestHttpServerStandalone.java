/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.http.HttpServerHandlerSetup;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRoute;
import net.jun0rr.doxy.http.HttpServer;
import net.jun0rr.doxy.http.util.RequestParam;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestHttpServerStandalone {
  
  @Test
  public void method() throws InterruptedException {
    try {
      //SSLHandlerFactory factory = SSLHandlerFactory.forServer(Paths.get("d:/java/doxy.jks"), "32132155".toCharArray());
      SSLHandlerFactory factory = SSLHandlerFactory.forServer(Paths.get("/home/juno/java/doxy.jks"), "32132155".toCharArray());
      ChannelHandlerSetup<HttpHandler> setup = HttpServerHandlerSetup.newSetup()
          .addOutputHandler(()->x->{
            System.out.println("[SERVER] HttpHandler (R1) >>> " + x.response().message());
            return x.forward();
          })
          .addConnectHandler(()->x->{
            System.out.println("[SERVER] Request received: " + x.channel().remoteHost());
          })
          .addRouteHandler(HttpRoute.post("/.*"), ()->x->{
            System.out.println("[ROUTE-A] POST /.*");
            x.response().headers().add("x-routeA", x.request().uri());
            return x.withResponse(x.response().withMessage(x.request().message())).sendAndClose();
          })
          .addRouteHandler(HttpRoute.get("/echo.*"), ()->x->{
            System.out.println("[ROUTE-B] GET /echo.*");
            RequestParam pars = RequestParam.fromUriQueryString(x.request().uri());
            x.response().headers().set(CONNECTION, CLOSE);
            x.response().headers().add("x-routeB", x.request().uri());
            return x.withResponse(x.response().withMessage(pars.get("message").toString())).sendAndClose();
          })
          .addRouteHandler(HttpRoute.any("/release/\\w+"), ()->x->{
            System.out.println("[ROUTE-C] ANY /release/\\w+");
            RequestParam par = RequestParam.fromUriPattern("/release/{cid}", x.request().uri());
            x.response().headers().set(CONNECTION, CLOSE);
            x.response().headers().add("x-routeC", x.request().uri());
            x.response().headers().add("x-channel-id", par.get("cid").toString());
            return x.withResponse(x.response().withMessage(x.request().message())).sendAndClose();
          })
          .enableSSL(factory)
          .addInputHandler(()->x->{
            System.out.printf("[SERVER] HttpHandler (F1) >>> [%s] %s - %s%n", x.request().method(), x.request().uri(), x.request().protocolVersion());
            x.request().headers().forEach(e->System.out.printf("   - %s: %s%n", e.getKey(), e.getValue()));
            return x.forward();
          });
      HttpServer server = HttpServer.open(setup);
      server.bind(Host.of("0.0.0.0:4321"))
          .onComplete(c->System.out.println("[SERVER] listening on: " + c.channel().localHost()))
          .executeSync();
      server.eventChain().awaitShutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
