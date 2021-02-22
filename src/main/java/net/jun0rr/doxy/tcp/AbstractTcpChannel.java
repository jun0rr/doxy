/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author juno
 */
public class AbstractTcpChannel implements TcpChannel {
  
  protected final EventLoopGroup group;
  
  protected SocketAddress local;
  
  protected SocketAddress remote;
  
  protected final EventContext context;
  
  protected Channel nettyChannel;
  
  public AbstractTcpChannel(EventLoopGroup group, EventLoop el) {
    this.group = Objects.requireNonNull(group, "Bad null EventLoopGroup");
    this.context = new TcpEventContext(this, el);
  }
  
  public AbstractTcpChannel(EventLoopGroup group) {
    this.group = Objects.requireNonNull(group, "Bad null EventLoopGroup");
    this.context = new TcpEventContext(this);
  }
  
  /**
   * Throws an IllegalStateException if channel is NOT created.
   */
  protected void channelCreated() {
    if(nettyChannel == null) throw new IllegalStateException("Channel not connected");
  }
  
  /**
   * Throws an IllegalStateException if channel is created.
   */
  protected void channelNotCreated() {
    if(nettyChannel != null) throw new IllegalStateException("Channel already connected");
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
    return nettyChannel;
  }
  
  @Override
  public EventChain events() {
    channelCreated();
    return new TcpEventChain(context);
  }
  
  @Override
  public EventChain closeFuture() {
    channelCreated();
    context.future(nettyChannel.closeFuture());
    return new TcpEventChain(context);
  }
  
  @Override
  public void close() throws Exception {
    events().close().execute();
  }

  @Override
  public Host localHost() {
    channelCreated();
    InetSocketAddress addr = (InetSocketAddress) ((ChannelFuture)context.future()).channel().localAddress();
    return Host.of(addr.getHostString(), addr.getPort());
  }

  @Override
  public Host remoteHost() {
    channelCreated();
    InetSocketAddress addr = (InetSocketAddress) ((ChannelFuture)context.future()).channel().remoteAddress();
    return Host.of(addr.getHostString(), addr.getPort());
  }

  @Override
  public EventContext context() {
    return context;
  }
  
}
