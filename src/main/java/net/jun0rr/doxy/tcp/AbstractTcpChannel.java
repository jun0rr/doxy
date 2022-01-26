/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Objects;
import net.jun0rr.doxy.cfg.Host;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import us.pserver.tools.LazyFinal;


/**
 *
 * @author juno
 */
public abstract class AbstractTcpChannel implements TcpChannel {
  
  public static final String SESSION_KEY_CHANNEL = "channel";
  
  protected final EventLoopGroup group;
  
  protected final LazyFinal<SocketAddress> local;
  
  protected final LazyFinal<SocketAddress> remote;
  
  protected final LazyFinal<ChannelFuture> future;
  
  protected final CacheManager manager;
  
  protected final TcpSession session;
  
  public AbstractTcpChannel(EventLoopGroup group) {
    this.group = Objects.requireNonNull(group, "Bad null EventLoopGroup");
    this.manager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
    CacheConfiguration<String,Object> cfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Object.class, ResourcePoolsBuilder.heap(500)).build();
    this.session = TcpSession.of(this, manager.createCache(getClass().getName(), cfg));
    this.local = new LazyFinal();
    this.remote = new LazyFinal();
    this.future = new LazyFinal();
  }
  
  public AbstractTcpChannel(EventLoopGroup group, ChannelFuture cf, TcpSession sess) {
    this.group = Objects.requireNonNull(group, "Bad null EventLoopGroup");
    this.manager = null;
    this.local = new LazyFinal();
    this.remote = new LazyFinal();
    this.future = new LazyFinal();
    this.initChannel(cf);
    this.session = TcpSession.prefixed(this, Objects.requireNonNull(sess));
    
  }
  
  protected void initChannel(ChannelFuture cf) {
    this.future.init(Objects.requireNonNull(cf));
    System.out.println("[AbstractTcpChannel.initChannel] future=" + future);
    this.local.init(cf.channel().localAddress());
    System.out.println("[AbstractTcpChannel.initChannel] local=" + local);
    this.remote.init(cf.channel().remoteAddress());
    System.out.println("[AbstractTcpChannel.initChannel] remote=" + remote);
    GenericFutureListener cleanup = f->session.clear();
    if(manager != null) {
      cleanup = f->{
        session.clear();
        manager.close();
      };
    }
    ChannelFuture closing = this.future.get().channel().closeFuture();
    if(!closing.isDone()) {
      closing.addListener(cleanup);
    }
  }
  
  /**
   * Throws an IllegalStateException if channel is NOT created.
   */
  protected void failOnChannelEmpty() {
    if(!future.isInitialized()) throw new IllegalStateException("Channel not connected");
  }
  
  /**
   * Throws an IllegalStateException if channel is created.
   */
  protected void failOnChannelInitialized() {
    if(future.isInitialized()) throw new IllegalStateException("Channel already connected");
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
    failOnChannelEmpty();
    return future.get().channel();
  }
  
  @Override
  public TcpEvents events() {
    failOnChannelEmpty();
    return TcpEvents.of(this, future.get());
  }
  
  @Override
  public TcpEvents closeFuture() {
    failOnChannelEmpty();
    return TcpEvents.of(this, future.get().channel().closeFuture());
  }
  
  @Override
  public Host localHost() {
    failOnChannelEmpty();
    return Host.of(local.get());
  }

  @Override
  public Host remoteHost() {
    failOnChannelEmpty();
    return Host.of(remote.get());
  }
  
  @Override
  public TcpSession session() {
    return session;
  }

}
