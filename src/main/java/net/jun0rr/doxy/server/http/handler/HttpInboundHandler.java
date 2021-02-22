/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server.http.handler;

import net.jun0rr.doxy.tcp.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Objects;
import net.jun0rr.doxy.common.InstanceOf;
import net.jun0rr.doxy.server.http.HttpExchange;
import net.jun0rr.doxy.server.http.HttpHandler;
import net.jun0rr.doxy.server.http.HttpRequest;
import net.jun0rr.doxy.server.http.HttpResponse;


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
    ConnectedTcpChannel cnc = new ConnectedTcpChannel(ctx);
    HttpResponse res = HttpResponse.of(HttpResponseStatus.OK);
    return InstanceOf.of(HttpRequest.class, r->HttpExchange.of(channel, cnc, ctx, r, res))
        .elseOf(FullHttpRequest.class, r->HttpExchange.of(channel, cnc, ctx, HttpRequest.of(r), res))
        .elseOf(HttpResponse.class, r->{
          HttpRequest req = HttpRequest.CURRENT_REQUEST.get() != null
              ? HttpRequest.CURRENT_REQUEST.get()
              : HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
          return HttpExchange.of(channel, cnc, ctx, req, r);
        })
        .elseOf(FullHttpResponse.class, r->{
          HttpRequest req = HttpRequest.CURRENT_REQUEST.get() != null
              ? HttpRequest.CURRENT_REQUEST.get()
              : HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
          return HttpExchange.of(channel, cnc, ctx, req, HttpResponse.of(r));
        })
        .elseOf(HttpExchange.class, x->x)
        .elseThrow(o->new HttpInboundException("Unknown message type: %s", o))
        .apply(msg).get();
  }
  
  @Override 
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
