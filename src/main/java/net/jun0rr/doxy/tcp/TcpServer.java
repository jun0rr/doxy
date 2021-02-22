/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author juno
 */
public class TcpServer extends AbstractBootstrapChannel {
  
  public TcpServer(ServerBootstrap boot, ChannelHandlerSetup<TcpHandler> setup) {
    super(boot, boot.config().childGroup().next(), setup);
  }
  
  public static TcpServer open(ChannelHandlerSetup<TcpHandler> setup) {
    return new TcpServer(serverBootstrap(
        new NioEventLoopGroup(1), 
        new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2)), 
        setup
    );
  }
  
  public static TcpServer open(EventLoopGroup parent, EventLoopGroup child, ChannelHandlerSetup<TcpHandler> setup) {
    return open(serverBootstrap(parent, child), setup);
  }
  
  public static TcpServer open(ServerBootstrap boot, ChannelHandlerSetup<TcpHandler> setup) {
    return new TcpServer(boot, setup);
  }
  
  public EventChain bind(Host host) {
    ChannelFuture cf = setupServerBootstrap().bind(host.toSocketAddr());
    this.nettyChannel = cf.channel();
    this.context.future(cf);
    return events();
  }
  
  public EventLoopGroup childGroup() {
    return ((ServerBootstrap)boot).config().childGroup();
  }
  
}
