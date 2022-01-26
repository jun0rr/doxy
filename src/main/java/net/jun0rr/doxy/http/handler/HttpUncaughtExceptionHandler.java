/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import net.jun0rr.doxy.http.HttpExchange;


/**
 *
 * @author Juno
 */
public class HttpUncaughtExceptionHandler extends ChannelInboundHandlerAdapter {
  
  private final BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtHandler;
  
  private final InternalLogger log;
  
  public HttpUncaughtExceptionHandler(BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtHandler) {
    this.log = InternalLoggerFactory.getInstance(getClass());
    this.uncaughtHandler = Objects.requireNonNull(uncaughtHandler, "Bad null UncaughtExceptionHandler BiFunction<HttpExchange,Throwable,Optional<HttpResponse>>");
  }
  
  public HttpUncaughtExceptionHandler() {
    this.log = InternalLoggerFactory.getInstance(getClass());
    this.uncaughtHandler = (x,t)->{ 
      log.error(new HttpUncaughtException(t)); 
      return Optional.empty(); 
    };
  }
  
  @Override 
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ctx.writeAndFlush(msg);
  }
  
  @Override 
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    uncaughtHandler.apply(HttpExchange.CURRENT_EXCHANGE.get(), e).ifPresent(ctx::writeAndFlush);
  }
  
  
  
  public class HttpUncaughtException extends RuntimeException {
    
    public HttpUncaughtException(Throwable cause) {
      super(String.join(": ", cause.getClass().getName(), cause.getMessage()), cause);
    }
    
  }
  
}
