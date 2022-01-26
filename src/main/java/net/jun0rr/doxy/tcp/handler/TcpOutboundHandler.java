/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.jun0rr.doxy.tcp.ConnectedTcpChannel;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpExchange;
import net.jun0rr.doxy.tcp.TcpHandler;


/**
 *
 * @author Juno
 */
public class TcpOutboundHandler extends ChannelOutboundHandlerAdapter {
  
  private final TcpChannel channel;
  
  private final TcpHandler handler;
  
  private final InternalLogger log;
  
  private final BiConsumer<ChannelHandlerContext,Throwable> errorHandler;
  
  public TcpOutboundHandler(TcpChannel channel, TcpHandler handler, BiConsumer<ChannelHandlerContext,Throwable> errorHandler) {
    this.log = InternalLoggerFactory.getInstance(getClass());
    this.handler = Objects.requireNonNull(handler, "Bad null TcpHandler");
    this.channel = Objects.requireNonNull(channel, "Bad null TcpChannel");
    this.errorHandler = errorHandler != null ? errorHandler 
        : (c,e)->log.error(new TcpOutboundException(e));
  }
  
  public TcpOutboundHandler(TcpChannel channel, TcpHandler handler) {
    this(channel, handler, null);
  }
  
  private TcpExchange exchange(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) {
    return (msg instanceof TcpExchange) 
        ? (TcpExchange) msg 
        : TcpExchange.of(channel, new ConnectedTcpChannel(ctx, channel.session(), cp), msg);
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) throws Exception {
    try {
      handler.apply(exchange(ctx, msg, cp)).ifPresent(x->ctx.write(x, cp));
    }
    catch(Throwable e) {
      this.exceptionCaught(ctx, e);
    }
  }
  
  @Override 
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    errorHandler.accept(ctx, e);
  }
  
  
  
  public class TcpOutboundException extends RuntimeException {
    
    public TcpOutboundException(Throwable cause) {
      super(String.join(": ", cause.getClass().getName(), cause.getMessage()), cause);
    }
    
  }
  
}
