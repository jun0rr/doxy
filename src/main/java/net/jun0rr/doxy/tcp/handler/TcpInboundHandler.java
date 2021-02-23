/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Objects;
import net.jun0rr.doxy.tcp.ConnectedTcpChannel;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpExchange;
import net.jun0rr.doxy.tcp.TcpHandler;


/**
 *
 * @author Juno
 */
public class TcpInboundHandler extends ChannelInboundHandlerAdapter {
  
  private final TcpChannel channel;
  
  private final TcpHandler handler;
  
  public TcpInboundHandler(TcpChannel chn, TcpHandler hnd) {
    this.handler = Objects.requireNonNull(hnd, "Bad null TcpHandler");
    this.channel = Objects.requireNonNull(chn, "Bad null TcpChannel");
  }
  
  private TcpExchange exchange(ChannelHandlerContext ctx, Object msg) {
    return (msg instanceof TcpExchange) 
        ? (TcpExchange) msg 
        : TcpExchange.of(channel, new ConnectedTcpChannel(ctx), msg);
  }
  
  @Override 
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    handler.apply(exchange(ctx, msg)).ifPresent(ctx::fireChannelRead);
  }
  
}
