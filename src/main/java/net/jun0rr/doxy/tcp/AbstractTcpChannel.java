/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
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
  
  protected final LazyFinal<EventContext> context;
  
  protected final LazyFinal<Channel> nettyChannel;
  
  public AbstractTcpChannel(EventLoopGroup group) {
    this.group = Objects.requireNonNull(group, "Bad null EventLoopGroup");
    this.context = new LazyFinal();
    this.nettyChannel = new LazyFinal();
  }
  
  protected void initChannel(Channel c, Future f) {
    this.nettyChannel.init(c);
    this.context.init(new TcpEventContext(new TcpEvent(this, f)));
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
  public EventContext events() {
    failOnChannelEmpty();
    return context.get();
  }
  
  @Override
  public EventContext closeFuture() {
    failOnChannelEmpty();
    return new TcpEventContext(new TcpEvent(
        context.get().channel(), 
        nettyChannel.get().closeFuture())
    );
  }
  
  @Override
  public Host localHost() {
    failOnChannelEmpty();
    InetSocketAddress addr = (InetSocketAddress) nettyChannel().localAddress();
    return Host.of(addr.getHostString(), addr.getPort());
  }

  @Override
  public Host remoteHost() {
    failOnChannelEmpty();
    InetSocketAddress addr = (InetSocketAddress) nettyChannel().remoteAddress();
    return Host.of(addr.getHostString(), addr.getPort());
  }

}
