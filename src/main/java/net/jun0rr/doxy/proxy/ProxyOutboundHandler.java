/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.function.Consumer;

/**
 *
 * @author juno
 */
public class ProxyOutboundHandler extends ChannelOutboundHandlerAdapter {

  public static final Consumer<ChannelErrorContext> DEFAULT_ERROR_LOGGER = e->e.logger().debug(e.error());
  
  private final InternalLogger logger;
  
  private final Consumer<ChannelWriteContext> writeHandler;
  
  private final Consumer<ChannelErrorContext> errorHandler;
  
  private ProxyOutboundHandler(
      Consumer<ChannelWriteContext> writeHandler,
      Consumer<ChannelErrorContext> errorHandler
  ) {
    this.writeHandler = writeHandler;
    this.errorHandler = errorHandler;
    this.logger = InternalLoggerFactory.getInstance(this.getClass());
  }
  
  public static ProxyOutboundHandler of(
      Consumer<ChannelWriteContext> writeHandler,
      Consumer<ChannelErrorContext> errorHandler
  ) {
    return new ProxyOutboundHandler(writeHandler, errorHandler);
  }
  
  public static ProxyOutboundHandler of(
      Consumer<ChannelWriteContext> writeHandler
  ) {
    return new ProxyOutboundHandler(writeHandler, DEFAULT_ERROR_LOGGER);
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object o, ChannelPromise cp) throws Exception {
    if(errorHandler != null) {
      writeHandler.accept(new ChannelWriteContext(ctx, o, cp));
    }
    ctx.write(o, cp);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable thrwbl) throws Exception {
    if(errorHandler != null) {
      errorHandler.accept(new ChannelErrorContext(ctx, logger, thrwbl));
    }
  }
  
}
