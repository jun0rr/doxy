/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.Objects;
import java.util.function.UnaryOperator;


/**
 *
 * @author Juno
 */
public class ConnectedTcpEvents extends TcpEvents {
  
  private final ChannelHandlerContext context;
  
  private final ChannelPromise promise;
  
  public ConnectedTcpEvents(TcpChannel channel, ChannelHandlerContext ctx, ChannelPromise cp) {
    super(channel, null, null, (cp != null ? cp : ctx.newSucceededFuture()));
    this.context = Objects.requireNonNull(ctx, "Bad null ChannelHandlerContext");
    this.promise = cp;
  }
  
  public ConnectedTcpEvents(TcpChannel channel, ChannelHandlerContext ctx) {
    this(channel, ctx, null);
  }
  
  @Override
  public TcpEvents write(Object msg) {
    UnaryOperator<ChannelFuture> op = f->context.write(msg);
    if(promise != null) op = f->context.write(msg, promise);
    return applyNext(op);
  }
  
  @Override
  public TcpEvents writeAndFlush(Object msg) {
    UnaryOperator<ChannelFuture> op = f->context.writeAndFlush(msg);
    if(promise != null) op = f->context.writeAndFlush(msg, promise);
    return applyNext(op);
  }
  
  @Override
  public TcpEvents flush() {
    return onComplete(f->context.flush());
  }
  
}
