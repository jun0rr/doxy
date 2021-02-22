/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author juno
 */
public class ConnectedTcpChannel extends AbstractTcpChannel {
  
  private final ChannelHandlerContext context;
  
  private final EventContext econtext;
  
  protected final ChannelPromise promise;
  
  public ConnectedTcpChannel(ChannelHandlerContext ctx, ChannelPromise prms) {
    super(Objects.requireNonNull(ctx, "Bad null ChannelHandlerContext")
        .channel().eventLoop().parent());
    this.context = ctx;
    this.promise = prms;
    this.econtext = new TcpEventContext(this, (prms != null) ? prms : ctx.newSucceededFuture());
    this.nettyChannel = ctx.channel();
  }
  
  public ConnectedTcpChannel(ChannelHandlerContext ctx) {
    this(ctx, null);
  }
  
  public ConnectedTcpChannel withPromise(ChannelPromise prms) {
    return new ConnectedTcpChannel(context, prms);
  }
  
  @Override
  public EventChain events() {
    return new ContextEventChain(econtext, context, promise);
  }
  
  /**
   * Return the main group.
   * @return Main EventLoopGroup.
   */
  @Override
  public EventLoopGroup group() {
    return group;
  }
  
  @Override
  public Channel nettyChannel() {
    return context.channel();
  }
  
  @Override
  public Host remoteHost() {
    return Host.of(context.channel().remoteAddress());
  }
  
  public Optional<ChannelPromise> promise() {
    return Optional.ofNullable(promise);
  }

}
