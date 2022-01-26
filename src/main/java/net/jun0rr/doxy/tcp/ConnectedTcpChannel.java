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
  
  public ConnectedTcpChannel(ChannelHandlerContext ctx, TcpSession session, ChannelPromise prms) {
    super(Objects.requireNonNull(ctx, "Bad null ChannelHandlerContext")
        .channel().eventLoop().parent(), ((prms != null) ? prms : ctx.newSucceededFuture()), session);
    this.context = ctx;
    this.promise = prms;
  }
  
  public ConnectedTcpChannel(ChannelHandlerContext ctx, TcpSession session) {
    this(ctx, session, null);
  }
  
  public ChannelHandlerContext channelContext() {
    return context;
  }
  
  @Override
  public TcpEvents events() {
    return new ConnectedTcpEvents(this, context, promise);
  }
  
}
