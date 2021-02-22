/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.function.BiConsumer;


/**
 *
 * @author Juno
 */
public class TcpUcaughtExceptionHandler extends ChannelInboundHandlerAdapter {
  
  private final BiConsumer<ChannelHandlerContext,Throwable> uncaughtHandler;
  
  private final InternalLogger log;
  
  public TcpUcaughtExceptionHandler(BiConsumer<ChannelHandlerContext,Throwable> uncaughtHandler) {
    this.log = InternalLoggerFactory.getInstance(getClass());
    this.uncaughtHandler = uncaughtHandler != null ? uncaughtHandler 
        : (c,t)->log.error("Uncaught Exception", t);
  }
  
  public TcpUcaughtExceptionHandler() {
    this(null);
  }
  
  @Override 
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ctx.writeAndFlush(msg);
  }
  
  @Override 
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    uncaughtHandler.accept(ctx, e);
  }
  
}
