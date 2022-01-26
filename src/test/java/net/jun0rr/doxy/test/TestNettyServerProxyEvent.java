/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.proxy.ProxyEvent;
import org.junit.jupiter.api.Test;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class TestNettyServerProxyEvent {
  
  @Test
  public void server() {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      Host bind = Host.of("0.0.0.0:4322");
      ServerBootstrap sb = new ServerBootstrap();
      sb.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        //.handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                //p.addLast(new LoggingHandler(LogLevel.INFO));
                //p.addLast(serverHandler);
                p.addLast(new ChannelInboundHandlerAdapter() {
                  @Override
                  public void channelRead(ChannelHandlerContext ctx, java.lang.Object msg) {
                    ByteBuf buf = (ByteBuf) msg;
                    String s = buf.toString(StandardCharsets.UTF_8);
                    System.out.println("[SERVER] " + s);
                    ctx.channel().close();
                  }
                });
            }
        });
      ProxyEvent.of(sb.bind(bind.toSocketAddr()))
          .onComplete(f->System.out.println("[SERVER] Listening at: " + f.channel().localAddress()));
      bossGroup.awaitTermination(1, TimeUnit.DAYS);
      workerGroup.awaitTermination(1, TimeUnit.DAYS);
    }
    catch(Exception e) {
      e.printStackTrace();
      throw Unchecked.unchecked(e);
    }
  }
  
}
