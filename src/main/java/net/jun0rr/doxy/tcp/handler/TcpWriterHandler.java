/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import net.jun0rr.doxy.tcp.TcpExchange;


/**
 *
 * @author Juno
 */
public class TcpWriterHandler extends ChannelOutboundHandlerAdapter {
  
  private final InternalLogger log;
  
  public TcpWriterHandler() {
    this.log = InternalLoggerFactory.getInstance(getClass());
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) throws Exception {
    //log.info("message={}, promise={}", msg, cp);
    try {
      System.out.println("[-TcpWriterHandler.write-] Promise=" + cp);
      Object out = (msg instanceof TcpExchange) ? ((TcpExchange)msg).message() : msg;
      GenericFutureListener fl = f->{
        System.out.println("[--TcpWriterHandler.write--] Write Future completed! Promise=" + cp);
      };
      ctx.writeAndFlush(out, cp).addListener(fl);
    }
    catch(Exception e) {
      this.exceptionCaught(ctx, e);
    }
  }
  
  @Override 
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    log.error(new TcpOutboundException(e));
  }
  
  
  
  public class TcpOutboundException extends RuntimeException {
    
    public TcpOutboundException(Throwable cause) {
      super(String.join(": ", cause.getClass().getName(), cause.getMessage()), cause);
    }
    
  }
  
}
