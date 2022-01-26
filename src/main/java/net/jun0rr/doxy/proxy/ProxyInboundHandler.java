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
import java.util.function.Consumer;

/**
 *
 * @author juno
 */
public class ProxyInboundHandler extends ChannelInboundHandlerAdapter {
  
  public static final Consumer<ChannelErrorContext> DEFAULT_ERROR_LOGGER = e->e.logger().debug(e.error());
  
  private final InternalLogger logger;
  
  private final Consumer<ChannelErrorContext> errorHandler;
  
  private ProxyInboundHandler(Consumer<ChannelErrorContext> cs) {
    this.logger = InternalLoggerFactory.getInstance(this.getClass());
    this.errorHandler = cs != null ? cs : DEFAULT_ERROR_LOGGER;
  }
  
  public static ProxyInboundHandler of(Consumer<ChannelErrorContext> cs) {
    return new ProxyInboundHandler(cs);
  }
  
  public static ProxyInboundHandler newHandler() {
    return new ProxyInboundHandler(null);
  }
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("[ProxyInboundHandler.channelActive()]");
    ctx.fireChannelActive();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
    System.out.println("[ProxyInboundHandler.channelRead()] msg=" + o);
    ctx.fireChannelRead(o);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    System.out.println("[ProxyInboundHandler.channelReadComplete()]");
    ctx.fireChannelReadComplete();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable ex) throws Exception {
    System.out.println("[ProxyInboundHandler.exceptionCaught()] ex=" + ex);
    ex.printStackTrace();
    if(errorHandler != null) {
      errorHandler.accept(new ChannelErrorContext(ctx, logger, ex));
    }
    ctx.fireExceptionCaught(ex);
  }

}
