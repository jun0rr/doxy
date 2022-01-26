/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import net.jun0rr.doxy.tcp.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.Objects;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;


/**
 *
 * @author Juno
 */
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
  
  private final TcpChannel channel;
  
  private final HttpHandler handler;
  
  public HttpInboundHandler(TcpChannel chn, HttpHandler hnd) {
    this.handler = Objects.requireNonNull(hnd, "Bad null HttpHandler");
    this.channel = Objects.requireNonNull(chn, "Bad null TcpChannel");
  }
  
  private HttpExchange exchange(ChannelHandlerContext ctx, Object msg) {
    ConnectedTcpChannel cnc = new ConnectedTcpChannel(ctx, channel.session());
    //System.out.println("[HttpInboundHandler.exchange] msg=" + msg);
    HttpExchange ex;
    if(msg instanceof HttpRequest) {
      ex = HttpExchange.of(channel, cnc, (HttpRequest) msg);
    }
    else if(msg instanceof HttpResponse) {
      ex = HttpExchange.of(channel, cnc, (HttpResponse) msg);
    }
    else if(msg instanceof HttpExchange) {
      ex = (HttpExchange) msg;
    }
    else {
      ex = HttpExchange.of(channel, cnc, msg);
    }
    //System.out.println("[HttpInboundHandler.exchange] ex=" + ex);
    return ex;
  }
  
  @Override 
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //System.out.println("[HttpInboundHandler.channelRead] handler=" + String.format("%s@%d", handler.getClass().getName(), handler.hashCode()));
    handler.apply(exchange(ctx, msg)).ifPresent(ctx::fireChannelRead);
  }
  
  
  
  public class HttpInboundException extends RuntimeException {
    
    public HttpInboundException(Throwable e) {
      this(e.toString(), e);
    }
    
    public HttpInboundException(Throwable e, String msg) {
      super(msg, e);
    }
    
    public HttpInboundException(String msg) {
      super(msg);
    }
    
    public HttpInboundException(String msg, Object... args) {
      super(String.format(msg, args));
    }
    
    public HttpInboundException(Throwable e, String msg, Object... args) {
      super(String.format(msg, args));
    }
    
  }
  
}
