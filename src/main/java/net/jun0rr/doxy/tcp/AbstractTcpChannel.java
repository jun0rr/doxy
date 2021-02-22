/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import net.jun0rr.doxy.cfg.Host;
import us.pserver.tools.LazyFinal;


/**
 *
 * @author juno
 */
public class AbstractTcpChannel implements TcpChannel {
  
  protected final EventLoopGroup group;
  
  protected SocketAddress local;
  
  protected SocketAddress remote;
  
  protected final Event context;
  
  protected final LazyFinal<Channel> nettyChannel;
  
  public AbstractTcpChannel(EventLoopGroup group) {
    this.group = Objects.requireNonNull(group, "Bad null EventLoopGroup");
    this.context = new TcpEvent(this);
    this.nettyChannel = new LazyFinal();
  }
  
  protected void initChannel(Channel c) {
    this.nettyChannel.init(c);
  }
  
  /**
   * Throws an IllegalStateException if channel is NOT created.
   */
  protected void failOnChannelEmpty() {
    if(!nettyChannel.isInitialized()) throw new IllegalStateException("Channel not connected");
  }
  
  /**
   * Throws an IllegalStateException if channel is created.
   */
  protected void failOnChannelInitialized() {
    if(nettyChannel.isInitialized()) throw new IllegalStateException("Channel already connected");
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
    return nettyChannel.get();
  }
  
  @Override
  public EventChain events() {
    failOnChannelEmpty();
    return new TcpEventChain(context);
  }
  
  @Override
  public EventChain closeFuture() {
    failOnChannelEmpty();
    context.future(nettyChannel.get().closeFuture());
    return new TcpEventChain(context);
  }
  
  @Override
  public void close() throws Exception {
    events().close().execute();
  }

  @Override
  public Host localHost() {
    failOnChannelEmpty();
    InetSocketAddress addr = (InetSocketAddress) ((ChannelFuture)context.future()).channel().localAddress();
    return Host.of(addr.getHostString(), addr.getPort());
  }

  @Override
  public Host remoteHost() {
    failOnChannelEmpty();
    InetSocketAddress addr = (InetSocketAddress) ((ChannelFuture)context.future()).channel().remoteAddress();
    return Host.of(addr.getHostString(), addr.getPort());
  }

  @Override
  public Event context() {
    return context;
  }
  
}
