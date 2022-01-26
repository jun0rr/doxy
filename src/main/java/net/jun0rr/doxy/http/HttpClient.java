/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import net.jun0rr.doxy.tcp.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author juno
 */
public class HttpClient extends AbstractBootstrapChannel {
  
  public HttpClient(Bootstrap boot, ChannelHandlerSetup<HttpHandler> setup) {
    super(boot, setup);
  }
  
  public static HttpClient open(ChannelHandlerSetup<HttpHandler> setup) {
    return open(bootstrap(new NioEventLoopGroup(1)), setup);
  }
  
  public static HttpClient open(EventLoopGroup group, ChannelHandlerSetup<HttpHandler> setup) {
    return open(bootstrap(group), setup);
  }
  
  public static HttpClient open(Bootstrap boot, ChannelHandlerSetup<HttpHandler> setup) {
    return new HttpClient(boot, setup);
  }
  
  public TcpEvents connect(Host host) {
    failOnChannelInitialized();
    initChannel(setupBootstrap().connect(host.toSocketAddr()));
    return events();
  }
  
}
