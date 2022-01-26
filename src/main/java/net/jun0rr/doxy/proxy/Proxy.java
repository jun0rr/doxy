/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.jun0rr.doxy.cfg.Host;

/**
 *
 * @author juno
 */
public class Proxy {
  /*
  private final Host listen, target;
  
  private final Queue<ProxyChannels> channels;
  
  private final EventLoopGroup serverGroup;
  
  private final EventLoopGroup childGroup;
  
  private final EventLoopGroup targetGroup;
  
  public Proxy(Host listen, Host target) {
    this.listen = Objects.requireNonNull(listen);
    this.target = Objects.requireNonNull(target);
    this.serverGroup = new NioEventLoopGroup(3);
    this.childGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
    this.targetGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
    this.channels = new ConcurrentLinkedQueue<>();
  }
  
  public EventLoopGroup serverGroup() {
    return serverGroup;
  }
  
  public EventLoopGroup childGroup() {
    return childGroup;
  }
  
  public EventLoopGroup targetGroup() {
    return targetGroup;
  }
  
  public Host localHost() {
    return listen;
  }
  
  public Host targetHost() {
    return target;
  }
  
  private static Bootstrap bootstrap(EventLoopGroup group) {
    return new Bootstrap()
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
        .option(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .option(ChannelOption.AUTO_READ, Boolean.TRUE)
        .group(group);
  }
  
  private ServerBootstrap serverBootstrap(EventLoopGroup parent, EventLoopGroup child) {
    return new ServerBootstrap()
        .channel(NioServerSocketChannel.class)
        .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_READ, Boolean.TRUE)
        .group(parent, child);
  }
  
  //protected ServerBootstrap setupServerBootstrap() {
    //ServerBootstrap sb = (ServerBootstrap) boot;
    //return sb.childHandler(setup.create(this));
  //}
  
  //protected Bootstrap setupBootstrap() {
    //Bootstrap b = (Bootstrap) boot;
    //return b.handler(setup.create(this));
  //}
  
  private ChannelInitializer<SocketChannel> initProxy() {
    return new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel c) throws Exception {
        c.pipeline()
            .addLast(ProxyInboundHandler.newHandler());
      }
    };
  }
  
  private ChannelInitializer<SocketChannel> initTarget() {
    return new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel c) throws Exception {
        c.pipeline()
            .addLast(ProxyInboundHandler.newHandler());
      }
    };
  }
  
  private ProxyEventChain connectTarget() {
    return ProxyEventChain.of(bootstrap(targetGroup)
        .handler(initTarget())
        .connect(target.toSocketAddr()))
        .onComplete(c->System.out.println("[TARGET] Connected at: " + c.channel().remoteAddress()));
  }
  
  private Consumer<ChannelHandlerContext> proxyConnectHandler() {
    return ctx->{
      System.out.println("[CLIENT] Connected: " + ctx.channel().remoteAddress());
      ProxyEventChain.of(ctx.channel().closeFuture()).onComplete(c->{
        Optional<ProxyChannels> opt = channels.stream()
            .filter(t->t.matchClient(ctx.channel()))
            .findAny();
        if(opt.isPresent()) {
          opt.get().target().close().execute();
          channels.remove(opt.get());
        }
      }).execute();
      channels.offer(ProxyChannels.of(ProxyEventChain.of(ctx.newSucceededFuture()), connectTarget()));
    };
  }
  
  private BiConsumer<ChannelHandlerContext,Object> proxyReadHandler() {
    return (ctx,o)->{
      System.out.println("[CLIENT] Read: " + o);
      channels.stream()
          .filter(t->t.matchClient(ctx.channel()))
          .findAny()
          .ifPresent(t->t.target().write(o).onComplete(c->System.out.println("[TARGET] Writed!")).execute());
    };
  }
  
  private Consumer<ChannelHandlerContext> proxyReadCompleteHandler() {
    return ctx->{
      System.out.println("[CLIENT] Read complete!");
      channels.stream()
          .filter(t->t.matchClient(ctx.channel()))
          .findAny()
          .ifPresent(t->t.target().flush().onComplete(c->System.out.println("[TARGET] Flushed!")).execute());
    };
  }
  
  private BiConsumer<ChannelHandlerContext,Object> targetReadHandler() {
    return (ctx,o)->{
      System.out.println("[TARGET] Read: " + o);
      channels.stream()
          .filter(t->t.matchTarget(ctx.channel()))
          .findAny()
          .ifPresent(t->t.client().write(o).onComplete(c->System.out.println("[CLIENT] Writed!")).execute());
    };
  }
  
  private Consumer<ChannelHandlerContext> targetReadCompleteHandler() {
    return ctx->{
      System.out.println("[TARGET] Read complete!");
      channels.stream()
          .filter(t->t.matchTarget(ctx.channel()))
          .findAny()
          .ifPresent(t->t.client().flush().onComplete(c->System.out.println("[CLIENT] Flushed!")).execute());
    };
  }
  
  public ProxyEventChain start() {
    return ProxyEventChain.of(serverBootstrap(serverGroup, childGroup)
        .childHandler(initProxy())
        .bind(listen.toSocketAddr()));
  }
  
  public void awaitShutdown() {
    serverGroup.terminationFuture().syncUninterruptibly();
  }
  */
}
