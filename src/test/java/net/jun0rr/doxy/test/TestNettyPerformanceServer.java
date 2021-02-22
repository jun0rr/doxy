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
import java.util.concurrent.atomic.AtomicBoolean;
import net.jun0rr.doxy.cfg.Host;
import org.junit.jupiter.api.Test;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class TestNettyPerformanceServer {
  /*
  private ChannelFuture future = null;
  
  @Test
  public void server() {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      Host bind = Host.of("0.0.0.0:4322");
      AtomicBoolean closing = new AtomicBoolean(false);
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
                    //String s = buf.toString(StandardCharsets.UTF_8);
                    //System.out.println("[SERVER] " + s);
                    //if(s.length() == 12) System.out.println("[SERVER] Client says: " + s);
                    if("!!SHUTDOWN!!".equals(buf.toString(StandardCharsets.UTF_8)) && closing.compareAndSet(false, true)) {
                      buf.release();
                      System.out.println("[SERVER] closing...");
                      ctx.channel().close();
                      future.channel().close();
                    }
                    else {
                      ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
                    }
                  }
                });
            }
        });
      future = sb.bind(bind.toSocketAddr());
      future.addListener(f->System.out.println("[SERVER] Listening at: " + ((ChannelFuture)f).channel().localAddress()));
      //sf = sf.sync();
      future.channel().closeFuture().sync();
      System.out.println("[SERVER] SHUTDOWN!");
      //if(future != null) return;
      
      //System.out.println("* Waiting CountDown...");
      //int cc = COUNT;
      //int retry = 80;
      //while(COUNT > 0) {
        //if(retry <= 0) {
          //System.out.println("# ERROR server(): COUNT(" + COUNT + ") is static for " + 80*50 + " millis...");
          //break;
        //}
        //Thread.sleep(50);
        //if(COUNT == cc) {
          //retry--;
        //}
        //cc = COUNT;
      //}
      //Thread.sleep(8000);
      //cd.await();
      //future.channel().close().sync();
      bossGroup.shutdownGracefully().sync();
      workerGroup.shutdownGracefully().sync();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw Unchecked.unchecked(e);
    }
  }
  */
}
