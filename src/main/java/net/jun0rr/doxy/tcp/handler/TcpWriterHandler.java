/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.jun0rr.doxy.tcp.TcpExchange;


/**
 *
 * @author Juno
 */
public class TcpWriterHandler extends ChannelOutboundHandlerAdapter {
  
  public static final Predicate<String> CONN_CLOSED_MSG = Pattern.compile("(Conex.{2}|Connection)\\s(fechada|close).*").asPredicate();
  
  private final InternalLogger log;
  
  public TcpWriterHandler() {
    this.log = InternalLoggerFactory.getInstance(getClass());
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) throws Exception {
    //log.info("message={}, promise={}", msg, cp);
    try {
      Object out = msg;
      if(out instanceof TcpExchange) {
        out = ((TcpExchange)msg).message();
      }
      if(out instanceof CharSequence) {
        out = Unpooled.copiedBuffer((String)out, StandardCharsets.UTF_8);
      }
      if(out != null) {
        ctx.write(out, cp);
      }
    }
    catch(Exception e) {
      this.exceptionCaught(ctx, e);
    }
  }
  
  @Override 
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    if(e instanceof IOException && !CONN_CLOSED_MSG.test(e.getMessage())) {
      log.warn(e);
    }
  }
  
}
