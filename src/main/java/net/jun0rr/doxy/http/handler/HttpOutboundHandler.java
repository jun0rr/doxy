/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRequest;
import net.jun0rr.doxy.http.HttpResponse;
import net.jun0rr.doxy.tcp.ConnectedTcpChannel;
import net.jun0rr.doxy.tcp.TcpChannel;


/**
 *
 * @author Juno
 */
public class HttpOutboundHandler extends ChannelOutboundHandlerAdapter {
  
  private final TcpChannel boot;
  
  private final HttpHandler handler;
  
  private final BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtHandler;
  
  public HttpOutboundHandler(TcpChannel boot, HttpHandler handler, BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtHandler) {
    this.boot = Objects.requireNonNull(boot, "Bad null boot TcpChannel");
    this.uncaughtHandler = Objects.requireNonNull(uncaughtHandler, "Bad null UncaughtExceptionHandler BiFunction<HttpExchange,Throwable,Optional<HttpResponse>>");
    this.handler = Objects.requireNonNull(handler, "Bad null HttpHandler");
  }
  
  private HttpExchange exchange(ChannelHandlerContext ctx, Object msg, ChannelPromise pms) {
    ConnectedTcpChannel cnc = new ConnectedTcpChannel(ctx, pms);
    HttpExchange ex;
    if(msg instanceof HttpExchange) {
      ex = (HttpExchange) msg;
    }
    else if(msg instanceof HttpRequest) {
      ex = HttpExchange.of(boot, cnc, 
          (HttpRequest) msg, 
          HttpResponse.of(HttpResponseStatus.OK)
      );
    }
    else if(msg instanceof FullHttpRequest) {
      ex = HttpExchange.of(boot, cnc, 
          HttpRequest.of((FullHttpRequest)msg), 
          HttpResponse.of(HttpResponseStatus.OK)
      );
    }
    else if(msg instanceof HttpResponse) {
      ex = HttpExchange.CURRENT_EXCHANGE.get()
          .withResponse((HttpResponse)msg);
    }
    else if(msg instanceof FullHttpResponse) {
      ex = HttpExchange.CURRENT_EXCHANGE.get()
          .withResponse(HttpResponse.of((FullHttpResponse)msg));
    }
    else {
      throw new IllegalStateException(String.format("[%s] Unknown message type: %s", handler.getClass().getName(), msg.getClass()));
    }
    return ex;
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) throws Exception {
    try {
      handler.apply(exchange(ctx, msg, cp)).ifPresent(x->ctx.writeAndFlush(x, cp));
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
