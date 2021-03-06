/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.Objects;


/**
 *
 * @author juno
 */
public class ConnectedTcpChannel extends AbstractTcpChannel {
  
  private final ChannelHandlerContext context;
  
  protected final ChannelPromise promise;
  
  public ConnectedTcpChannel(ChannelHandlerContext ctx, ChannelPromise prms) {
    super(Objects.requireNonNull(ctx, "Bad null ChannelHandlerContext")
        .channel().eventLoop().parent());
    this.context = ctx;
    this.promise = prms;
    this.initChannel(ctx.channel(), (prms != null) ? prms : ctx.newSucceededFuture());
  }
  
  public ConnectedTcpChannel(ChannelHandlerContext ctx) {
    this(ctx, null);
  }
  
  public ChannelHandlerContext channelContext() {
    return context;
  }
  
  //@Override
  //public EventContext events() {
    //return new ConnectedTcpEventContext(this);
  //}
  
}
