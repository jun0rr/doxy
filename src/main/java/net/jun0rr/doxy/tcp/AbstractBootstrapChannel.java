/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Objects;


/**
 *
 * @author Juno
 */
public class AbstractBootstrapChannel extends AbstractTcpChannel {
  
  protected final AbstractBootstrap boot;
  
  protected final ChannelHandlerSetup<? extends ChannelHandler> setup;
  
  public AbstractBootstrapChannel(AbstractBootstrap boot, EventLoop loop, ChannelHandlerSetup<? extends ChannelHandler> setup) {
    super(Objects.requireNonNull(boot, "Bad null Bootstrap").config().group(), loop);
    this.boot = boot;
    this.setup = Objects.requireNonNull(setup, "Bad null ChannelHandlerSetup<TcpChannel,TcpHandler>");
  }
  
  public AbstractBootstrapChannel(AbstractBootstrap boot, ChannelHandlerSetup<? extends ChannelHandler> setup) {
    super(Objects.requireNonNull(boot, "Bad null Bootstrap").config().group());
    this.boot = boot;
    this.setup = Objects.requireNonNull(setup, "Bad null ChannelHandlerSetup<TcpChannel,TcpHandler>");
  }
  
  protected static Bootstrap bootstrap(EventLoopGroup group) {
    return new Bootstrap()
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
        .option(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .option(ChannelOption.AUTO_READ, Boolean.TRUE)
        .group(group);
  }
  
  protected static ServerBootstrap serverBootstrap(EventLoopGroup parent, EventLoopGroup child) {
    return new ServerBootstrap()
        .channel(NioServerSocketChannel.class)
        .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_READ, Boolean.TRUE)
        .group(parent, child);
  }
  
  protected ServerBootstrap setupServerBootstrap() {
    ServerBootstrap sb = (ServerBootstrap) boot;
    return sb.childHandler(setup.create(this));
  }
  
  protected Bootstrap setupBootstrap() {
    Bootstrap b = (Bootstrap) boot;
    return b.handler(setup.create(this));
  }
  
}
