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
  
  protected final Event event;
  
  protected final LazyFinal<Channel> nettyChannel;
  
  public AbstractTcpChannel(EventLoopGroup group) {
    this.group = Objects.requireNonNull(group, "Bad null EventLoopGroup");
    this.event = new TcpEvent(this);
    this.nettyChannel = new LazyFinal();
  }
  
  protected void initChannel(Channel c, Future f) {
    this.nettyChannel.init(c);
    event.future(f);
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
  public Event event() {
    return event;
  }
  
  @Override
  public EventChain eventChain() {
    failOnChannelEmpty();
    return new TcpEventChain(event);
  }
  
  @Override
  public EventChain closeFuture() {
    failOnChannelEmpty();
    return new TcpEventChain(new TcpEvent(this, nettyChannel.get().closeFuture()));
  }
  
  @Override
  public Host localHost() {
    failOnChannelEmpty();
    //System.out.printf("[AbstractTcpChannel.localHost] nettyChannel=%s%n", nettyChannel());
    //System.out.printf("[AbstractTcpChannel.localHost] nettyChannel=%s, localAddress=%s%n", nettyChannel(), nettyChannel().localAddress());
    //System.out.printf("[AbstractTcpChannel.localHost] nettyChannel=%s, localAddress=%s, localAddress.class=%s%n", nettyChannel(), nettyChannel().localAddress(), nettyChannel().localAddress().getClass().getSimpleName());
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
