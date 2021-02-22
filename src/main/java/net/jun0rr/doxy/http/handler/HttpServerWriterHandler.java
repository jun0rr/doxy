/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpResponse;


/**
 *
 * @author Juno
 */
public class HttpServerWriterHandler extends ChannelOutboundHandlerAdapter {
  
  private final DateTimeFormatter datefmt;
  
  private final BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtHandler;
  
  private final HttpHeaders headers;
  
  private final String serverName;
  
  public HttpServerWriterHandler(String serverName, HttpHeaders hds, BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtHandler) {
    this.serverName = Objects.requireNonNull(serverName, "Bad null server name String");
    this.headers = Objects.requireNonNull(hds, "Bad null HttpHeaders");
    this.uncaughtHandler = Objects.requireNonNull(uncaughtHandler, "Bad null UncaughtExceptionHandler BiFunction<HttpExchange,Throwable,Optional<HttpResponse>>");
    this.datefmt = DateTimeFormatter
        .ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz")
        .withZone(ZoneId.of("GMT"));
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) throws Exception {
    try {
      System.out.println("[HttpServerWriterHandler.write] message=" + msg);
      HttpResponse rsp;
      if(msg instanceof HttpExchange) {
        rsp = ((HttpExchange)msg).response();
      }
      else if(msg instanceof FullHttpResponse) {
        rsp = HttpResponse.of((FullHttpResponse) msg);
      }
      else if(msg instanceof HttpResponse) {
        rsp = (HttpResponse) msg;
      }
      else {
        throw new IllegalStateException("Bad message type: " + msg.getClass().getName());
      }
      if(rsp.message() != null) {
        System.out.println("[HttpServerWriterHandler.write] message=" + rsp.message());
        ByteBuf buf;
        if(rsp.message() instanceof CharSequence) {
          buf = Unpooled.copiedBuffer(rsp.<CharSequence>message(), StandardCharsets.UTF_8);
        }
        else {
          buf = rsp.message();
        }
        if(buf.readableBytes() > 0) {
          System.out.println("[HttpServerWriterHandler.write] message.readableBytes=" + buf.readableBytes());
          rsp.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
          rsp = rsp.withMessage(buf);
        }
      }
      if(!headers.isEmpty()) rsp.headers().add(headers);
      rsp.headers()
          .set(HttpHeaderNames.DATE, datefmt.format(Instant.now()))
          .set(HttpHeaderNames.SERVER, serverName);
      ctx.writeAndFlush(rsp, cp);
    }
    catch(Exception e) {
      this.exceptionCaught(ctx, e);
    }
  }
  
  @Override 
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    uncaughtHandler.apply(HttpExchange.CURRENT_EXCHANGE.get(), e)
        .ifPresent(x->ctx.writeAndFlush(x).addListener(ChannelFutureListener.CLOSE));
  }
  
}
