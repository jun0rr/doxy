/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import net.jun0rr.doxy.http.HttpExchange;


/**
 *
 * @author Juno
 */
public class HttpClientWriterHandler extends ChannelOutboundHandlerAdapter {
  
  private final InternalLogger log;
  
  public HttpClientWriterHandler() {
    this.log = InternalLoggerFactory.getInstance(getClass());
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) throws Exception {
    //log.info("message={}, promise={}", msg, cp);
    try {
      Object out = msg;
      if(out instanceof HttpExchange) {
        HttpExchange x = (HttpExchange) out;
        out = x.message() != null ? x.message() : x.request();
      }
      if(out instanceof String) {
        out = Unpooled.copiedBuffer((String)out, StandardCharsets.UTF_8);
      }
      //if(out instanceof ByteBuf) {
        //out = new DefaultHttpContent((ByteBuf) out);
      //}
      System.out.printf("[%s.write] out.class=%s, out=%s%n", this, out.getClass().getSimpleName(), out);
      ctx.writeAndFlush(out, cp).addListener(f->{
        System.out.printf("[%s.write] ERROR=%s%n", this, Objects.toString(f.cause()));
      });
    }
    catch(Exception e) {
      this.exceptionCaught(ctx, e);
    }
  }
  
  @Override 
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    log.error(new HttpOutboundException(e));
  }
  
  
  
  public class HttpOutboundException extends RuntimeException {
    
    public HttpOutboundException(Throwable cause) {
      super(String.join(": ", cause.getClass().getName(), cause.getMessage()), cause);
    }
    
  }
  
}
