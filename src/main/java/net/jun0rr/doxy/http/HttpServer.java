/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import net.jun0rr.doxy.tcp.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author juno
 */
public class HttpServer extends AbstractBootstrapChannel {
  
  public HttpServer(ServerBootstrap boot, ChannelHandlerSetup<HttpHandler> setup) {
    super(boot, setup);
  }
  
  public static HttpServer open(ChannelHandlerSetup<HttpHandler> setup) {
    return new HttpServer(serverBootstrap(
        new NioEventLoopGroup(1), 
        new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2)), 
        setup
    );
  }
  
  public static HttpServer open(EventLoopGroup parent, EventLoopGroup child, ChannelHandlerSetup<HttpHandler> setup) {
    return open(serverBootstrap(parent, child), setup);
  }
  
  public static HttpServer open(ServerBootstrap boot, ChannelHandlerSetup<HttpHandler> setup) {
    return new HttpServer(boot, setup);
  }
  
  public EventChain bind(Host host) {
    ChannelFuture cf = setupServerBootstrap().bind(host.toSocketAddr());
    this.nettyChannel = cf.channel();
    context.future(cf);
    return events();
  }
  
  public EventLoopGroup childGroup() {
    return ((ServerBootstrap)boot).config().childGroup();
  }
  
}
