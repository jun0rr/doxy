/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.http.HttpRoute;
import net.jun0rr.doxy.http.HttpServer;
import net.jun0rr.doxy.http.HttpServerHandlerSetup;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpChannelHandlerSetup;
import net.jun0rr.doxy.tcp.TcpClient;
import net.jun0rr.doxy.tcp.TcpEvents;
import net.jun0rr.doxy.tcp.TcpServer;
import org.junit.jupiter.api.Test;
import us.pserver.tools.FileSizeFormatter;

/**
 *
 * @author juno
 */
public class TestTcpProxy {
  
  private static void printRequest(HttpRequest req) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(req.method()).append(" ").append(req.uri()).append("]\n");
    req.headers().forEach(e->sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
    System.out.println(sb);
  }
  
  private static void printResponse(HttpResponse res) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(res.status()).append("]\n");
    res.headers().forEach(e->sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
    System.out.println(sb);
  }
  
  //@Test
  public TcpEvents httpServer() throws URISyntaxException {
    Path kspath = Paths.get(getClass().getClassLoader().getResource("doxy.jks").toURI());
    SSLHandlerFactory ssl = SSLHandlerFactory.forServer(kspath, "32132155".toCharArray());
    ChannelHandlerSetup setup = HttpServerHandlerSetup.newSetup()
        .enableSSL(ssl)
        .addReadCompleteHandler(x->{
          System.out.println("[HTTP] Read Complete!");
          return x.empty();
        })
        .addConnectHandler(x->{
          System.out.println("[HTTP] Client connected: " + x.channel().remoteHost());
        })
        .addInputHandler(x->{
          printRequest(x.request());
          System.out.printf("[FILTER-A] isHttpMessage=%s, isHttpContent=%s, isLastHttpContent=%s, isEmptyLastContent=%s%n", x.isHttpMessage(), x.isHttpContent(), x.isLastHttpContent(), x.isEmptyLastContent());
          x.session().put("isLastHttpContent", x.isLastHttpContent());
          System.out.printf("[FILTER-A] x.session().get('isLastHttpContent')=%s%n", x.session().get("isLastHttpContent"));
          if(x.isHttpContent()) {
            return x.message(x.<HttpContent>message().content().toString(StandardCharsets.UTF_8)).forward();
          }
          return x.forward();
        })
        .addInputHandler(x->{
          System.out.printf("[FILTER-B] isHttpMessage=%s, isHttpContent=%s, isLastHttpContent=%s, isEmptyLastContent=%s%n", x.isHttpMessage(), x.isHttpContent(), x.isLastHttpContent(), x.isEmptyLastContent());
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
          if(x.isHttpContent()) {
            sb.append("<h4>Message:</h4>")
                .append("<div style='width: 500px; height: 80px; padding: 10px 20px 10px 20px; border: 1px solid green; border-radius: 3px; font-family: monospace; font-size: 14px;'>")
                .append(x.<String>message())
                .append("</div>");
            System.out.println("[FILTER-B] HttpContent=" + sb.toString());
          }
          if(x.session().<Boolean>get("isLastHttpContent").get()) {
            x.session().put("resp", sb);
          }
          return x.forward();
        })
        .addRouteHandler(HttpRoute.any("/.*"), x->{
          System.out.println("[ROUTE /.*] x.session().get('resp')=" + x.session().get("resp"));
          if(x.session().get("resp").isPresent()) {
            System.out.println("-- Session entries --");
            x.session().entries().forEach(e->System.out.printf("  - %s=%s%n", e.getKey(), e.getValue()));
            return x.responseBuilder().ok()
                .addHeader(HttpHeaderNames.CONTENT_TYPE, "text/html")
                .addHeader(HttpHeaderNames.CONTENT_LENGTH, x.session().get("resp").get().toString().length())
                .bodyUtf8(x.session().get("resp").get().toString())
                .done()
                .sendAndClose();
          }
          return x.empty();
        })
        .addRouteHandler(HttpRoute.any("/shutdown.*"), x->{
          System.out.println("[ROUTE /shutdown.*] x.session().get('resp')=" + x.session().get("resp"));
          System.out.println("[HTTP] Shutting Down...");
          if(x.session().get("resp").isPresent()) {
            x.channel().closeFuture()
                .onComplete(e->x.bootstrapChannel()
                    .events()
                    .shutdown()
                    .onComplete(f->System.out.println("[HTTP] Shutdown Completed!")))
                .close();
            return x.responseBuilder()
                .ok()
                .addHeader(HttpHeaderNames.CONTENT_TYPE, "text/html")
                .addHeader(HttpHeaderNames.CONTENT_LENGTH, x.session().get("resp").get().toString().length())
                .body(Unpooled.copiedBuffer(x.session().get("resp").get().toString(), StandardCharsets.UTF_8))
                .done()
                .sendAndClose();
          }
          return x.empty();
        })
        .addOutputHandler(x->{
          //System.out.println("RESPONSE CLASS: " + x.response().getClass());
          return x.responseBuilder().addHeader("x-output-instant", Instant.now().toString()).done().forward();
        });
    return HttpServer.open(setup)
        .bind(Host.of("localhost", 4443))
        .onComplete(e->System.out.println("[HTTP] Listening on " + e.channel().localAddress()))
        .sync();
  }
  
  
  public TcpEvents proxy() {
    Map<Host,TcpEvents> conns = new ConcurrentHashMap();
    AtomicLong bytesIn = new AtomicLong(0L);
    AtomicLong bytesOut = new AtomicLong(0L);
    long startup = System.currentTimeMillis();
    FileSizeFormatter fmt = new FileSizeFormatter();
    AtomicReference<String> strIn = new AtomicReference(fmt.format(bytesIn.get()));
    AtomicReference<String> strOut = new AtomicReference(fmt.format(bytesOut.get()));
    ChannelHandlerSetup setup = TcpChannelHandlerSetup.newSetup()
        .addConnectHandler(x->{
          TcpChannel client = x.channel();
          System.out.println("[PROXY] Client Connected " + client.remoteHost());
          ChannelHandlerSetup targetSetup = TcpChannelHandlerSetup.newSetup()
              .addReadCompleteHandler(y->{
                client.events().flush();
                return x.empty();
              })
              .addInputHandler(y->{
                String sout = fmt.format(bytesOut.addAndGet(y.<ByteBuf>message().readableBytes()));
                if(!sout.equals(strOut.get())) {
                  strOut.set(sout);
                  long time = (System.currentTimeMillis() - startup) / 1000;
                  String sbps = fmt.format((bytesIn.get() + bytesOut.get()) / time);
                  System.out.printf("[PROXY] <%s> {Input: %s, Output: %s, Traffic: %s/sec}%n", client.remoteHost(), strIn.get(), sout, sbps);
                }
                client.events().write(y.message());
                return y.empty();
              });
          conns.put(client.remoteHost(), TcpClient.open(targetSetup)
              .connect(Host.of("localhost", 4443))
          );
          x.channel().closeFuture()
              .onComplete(e->conns.remove(client.remoteHost()).shutdown());
        })
        .addReadCompleteHandler(x->{
          conns.get(x.channel().remoteHost()).flush();
          return x.empty();
        })
        .addInputHandler(x->{
          byte[] conn = "CONNECT".getBytes(StandardCharsets.UTF_8);
          int ridx = x.<ByteBuf>message().readerIndex();
          int widx = x.<ByteBuf>message().writerIndex();
          byte[] start = new byte[conn.length];
          x.<ByteBuf>message().readBytes(start);
          x.<ByteBuf>message().readerIndex(ridx);
          x.<ByteBuf>message().writerIndex(widx);
          if(Arrays.equals(conn, start)) {
            return x.message(Unpooled.copiedBuffer("HTTP/1.1 200 Connection established\r\n\r\n", StandardCharsets.UTF_8))
                .sendAndFlush();
          }
          return x.forward();
        })
        .addInputHandler(x->{
          String sin = fmt.format(bytesIn.addAndGet(x.<ByteBuf>message().readableBytes()));
          if(!sin.equals(strIn.get())) {
            strIn.set(sin);
            long time = (System.currentTimeMillis() - startup) / 1000;
            String sbps = fmt.format((bytesIn.get() + bytesOut.get()) / time);
            System.out.printf("[PROXY] <%s> {Input: %s, Output: %s, Traffic: %s/sec}%n", x.channel().remoteHost(), strIn.get(), strOut.get(), sbps);
          }
          if(!sin.equals(strIn.get())) strIn.set(sin);
          conns.get(x.channel().remoteHost()).write(x.message());
          return x.empty();
        });
    return TcpServer.open(setup)
        .bind(Host.of("localhost", 4444))
        .onComplete(e->System.out.println("[PROXY] Listening on " + e.channel().localAddress()))
        .sync();
  }
  
  @Test
  public void test() throws URISyntaxException {
    TcpEvents http = httpServer();
    TcpEvents proxy = proxy();
    http.closeFuture().onComplete(e->proxy.shutdown()
          .onComplete(c->System.out.println("[PROXY] Shutdown Completed!")))
        .awaitShutdown();
  }
  
}
