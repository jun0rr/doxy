/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.GenericFutureListener;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author juno
 */
public class TcpClient extends AbstractBootstrapChannel {
  
  public TcpClient(Bootstrap boot, ChannelHandlerSetup<TcpHandler> setup) {
    super(boot, setup);
  }
  
  public static TcpClient open(ChannelHandlerSetup<TcpHandler> setup) {
    return open(bootstrap(new NioEventLoopGroup(1)), setup);
  }
  
  public static TcpClient open(EventLoopGroup group, ChannelHandlerSetup<TcpHandler> setup) {
    return open(bootstrap(group), setup);
  }
  
  public static TcpClient open(Bootstrap boot, ChannelHandlerSetup<TcpHandler> setup) {
    return new TcpClient(boot, setup);
  }
  
  public TcpEvents connect(Host host) {
    failOnChannelInitialized();
    GenericFutureListener<ChannelFuture> con = this::initChannel;
    setupBootstrap().connect(host.toSocketAddr()).addListener(con);
    return events();
  }

}
