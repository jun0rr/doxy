/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.tcp.ConnectedTcpChannel;
import net.jun0rr.doxy.tcp.TcpChannel;


/**
 *
 * @author Juno
 */
public class HttpReadCompleteHandler extends ChannelInboundHandlerAdapter {
  
  private final TcpChannel channel;
  
  private final HttpHandler handler;
  
  public HttpReadCompleteHandler(TcpChannel chn, HttpHandler hnd) {
    this.handler = Objects.requireNonNull(hnd, "Bad null TcpHandler");
    this.channel = Objects.requireNonNull(chn, "Bad null TcpChannel");
  }
  
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    SslHandler ssl = ctx.pipeline().get(SslHandler.class);
    //if(ssl != null && ssl.handshakeFuture().isDone() && ssl.handshakeFuture().isSuccess()) {
      //handler.apply(HttpExchange.of(channel, new ConnectedTcpChannel(ctx)))
        //.ifPresent(x->ctx.fireChannelReadComplete());
    //}
    GenericFutureListener lst = f->handler
        .apply(HttpExchange.of(channel, new ConnectedTcpChannel(ctx, channel.session())))
        .ifPresent(x->ctx.fireChannelReadComplete());
    if(ssl != null) {
      ssl.handshakeFuture().addListener(lst);
    }
    else {
      ctx.newSucceededFuture().addListener(lst);
    }
  }
  
}
