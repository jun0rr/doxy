/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.proxy.ProxyEvent;
import org.junit.jupiter.api.Test;
import us.pserver.tools.Sleeper;
import us.pserver.tools.Timer;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class TestNettyProxyEvent {
  
  @Test
  public void clients() {
    try {
      EventLoopGroup clients = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
      Host target = Host.of("localhost:4322");
      Bootstrap b = new Bootstrap();
      b.group(clients)
          //.group(new NioEventLoopGroup(1))
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
            }
          });
      System.out.printf("[TestNettyProxyEvent] Connecting to %s...%n", target);
      ProxyEvent.of(b.connect(target.toSocketAddr()))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Connected 1 " + c.channel().remoteAddress()))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Connected 2 " + c.channel().remoteAddress()))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Connected 3 " + c.channel().remoteAddress()))
          .writeAndFlush(Unpooled.copiedBuffer("HELLO WORLD", StandardCharsets.UTF_8))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Writed 1"))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Writed 2"))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Writed 3"))
          .flush()
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Flushed 1"))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Flushed 2"))
          .onComplete(c->System.out.println("[TestNettyProxyEvent] Channel Flushed 3"))
          .close()
          .onClose(c->System.out.printf("[TestNettyProxyEvent] Channel Closed 1 {active=%s, open=%s}%n", c.channel().isActive(), c.channel().isOpen()))
          .onClose(c->System.out.printf("[TestNettyProxyEvent] Channel Closed 2 {active=%s, open=%s}%n", c.channel().isActive(), c.channel().isOpen()))
          .onClose(c->System.out.printf("[TestNettyProxyEvent] Channel Closed 3 {active=%s, open=%s}%n", c.channel().isActive(), c.channel().isOpen()))
          ;
      Sleeper.of(1000).sleep();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw Unchecked.unchecked(e);
    }
  }
  
}
