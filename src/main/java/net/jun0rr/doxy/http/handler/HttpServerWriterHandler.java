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
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import net.jun0rr.doxy.http.HttpExchange;


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
      Object out = msg;
      if(out instanceof HttpExchange) {
        HttpExchange x = (HttpExchange) out;
        out = x.message() != null ? x.message() : x.response();
      }
      if(out instanceof HttpResponse) {
        HttpResponse rsp = (HttpResponse) out;
        if(!headers.isEmpty()) rsp.headers().add(headers);
        rsp.headers()
            .set(HttpHeaderNames.DATE, datefmt.format(Instant.now()))
            .set(HttpHeaderNames.SERVER, serverName);
      }
      if(out instanceof String) {
        out = Unpooled.copiedBuffer((String)out, StandardCharsets.UTF_8);
      }
      if(out instanceof ByteBuf) {
        out = new DefaultHttpContent((ByteBuf) out);
      }
      ctx.writeAndFlush(out, cp);
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
