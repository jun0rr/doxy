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
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.charset.StandardCharsets;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpRequest;


/**
 *
 * @author Juno
 */
public class HttpClientWriterHandler extends ChannelOutboundHandlerAdapter {
  
  private final InternalLogger log;
  
  public HttpClientWriterHandler() {
    this.log = InternalLoggerFactory.getInstance(getClass());
  }
  
  private HttpRequest request(Object msg) {
    HttpRequest req;
    if(msg instanceof HttpExchange) {
      req = ((HttpExchange)msg).request();
    }
    else if(msg instanceof HttpRequest) {
      req = (HttpRequest) msg;
    }
    else if(msg instanceof FullHttpRequest) {
      req = HttpRequest.of((FullHttpRequest)msg);
    }
    else {
      throw new IllegalStateException("Bad message type: " + msg.getClass().getName());
    }
    return req;
  }
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise cp) throws Exception {
    //log.info("message={}, promise={}", msg, cp);
    try {
      HttpRequest req = request(msg);
      if(req.message() != null) {
        ByteBuf buf;
        if(req.message() instanceof CharSequence) {
          buf = Unpooled.copiedBuffer(req.<CharSequence>message(), StandardCharsets.UTF_8);
        }
        else {
          buf = req.message();
        }
        if(buf.readableBytes() > 0) {
          req.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        }
      }
      ctx.writeAndFlush(req, cp);
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
