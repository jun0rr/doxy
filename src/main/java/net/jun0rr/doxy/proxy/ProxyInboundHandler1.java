/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author juno
 */
public class ProxyInboundHandler1 extends ChannelInboundHandlerAdapter {
  
  public static final Consumer<ChannelErrorContext> DEFAULT_ERROR_LOGGER = e->e.logger().debug(e.error());
  
  private final InternalLogger logger;
  
  private final BiConsumer<ChannelHandlerContext, Object> readHandler;
  
  private final Consumer<ChannelErrorContext> errorHandler;
  
  private final Consumer<ChannelHandlerContext> connectHandler;
  
  private final Consumer<ChannelHandlerContext> readCompleteHandler;
  
  private ProxyInboundHandler1(
      Consumer<ChannelHandlerContext> connectHandler,
      BiConsumer<ChannelHandlerContext, Object> readHandler, 
      Consumer<ChannelHandlerContext> readCompleteHandler,
      Consumer<ChannelErrorContext> errorHandler
  ) {
    this.connectHandler = connectHandler;
    this.readHandler = readHandler;
    this.errorHandler = errorHandler;
    this.readCompleteHandler = readCompleteHandler;
    this.logger = InternalLoggerFactory.getInstance(this.getClass());
  }
  
  public static ProxyInboundHandler1 of(
      Consumer<ChannelHandlerContext> connectHandler,
      BiConsumer<ChannelHandlerContext, Object> readHandler, 
      Consumer<ChannelHandlerContext> readCompleteHandler,
      Consumer<ChannelErrorContext> errorHandler
  ) {
    return new ProxyInboundHandler1(connectHandler, readHandler, readCompleteHandler, errorHandler);
  }

  public static ProxyInboundHandler1 of(
      Consumer<ChannelHandlerContext> connectHandler
  ) {
    return new ProxyInboundHandler1(connectHandler, null, null, DEFAULT_ERROR_LOGGER);
  }

  public static ProxyInboundHandler1 of(
      BiConsumer<ChannelHandlerContext, Object> readHandler, 
      Consumer<ChannelHandlerContext> readCompleteHandler
  ) {
    return new ProxyInboundHandler1(null, readHandler, readCompleteHandler, DEFAULT_ERROR_LOGGER);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("[ProxyInboundHandler.channelActive()]");
    if(connectHandler != null) {
      connectHandler.accept(ctx);
    }
    ctx.fireChannelActive();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
    System.out.println("[ProxyInboundHandler.channelRead()] msg=" + o);
    if(readHandler != null) {
      readHandler.accept(ctx, o);
    }
    ctx.fireChannelRead(o);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    System.out.println("[ProxyInboundHandler.channelReadComplete()]");
    if(readCompleteHandler != null) {
      readCompleteHandler.accept(ctx);
    }
    ctx.fireChannelReadComplete();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable ex) throws Exception {
    System.out.println("[ProxyInboundHandler.exceptionCaught()] ex=" + ex);
    if(errorHandler != null) {
      errorHandler.accept(new ChannelErrorContext(ctx, logger, ex));
    }
    ctx.fireExceptionCaught(ex);
  }

}
