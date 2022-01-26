/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import us.pserver.tools.Unchecked;

/**
 *
 * @author juno
 */
public class TcpEvents extends DefaultPromise<ChannelFuture> implements GenericFutureListener<Future<ChannelFuture>> {
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(TcpEvents.class);
  
  public static final BiConsumer<ChannelFuture,Throwable> DEFAULT_ERROR_HANDLER = (c,e)->logger.error(e);
  
  private final TcpChannel channel;
  
  private final ChannelFuture future;
  
  private final UnaryOperator<ChannelFuture> operator;
  
  private final BiConsumer<ChannelFuture,Throwable> errorHandler;
  
  protected TcpEvents(TcpChannel channel, UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> errorHandler, ChannelFuture cf) {
    super(Objects.requireNonNull(channel).group().next());
    this.channel = channel;
    this.operator = op;
    this.errorHandler = errorHandler;
    this.future = cf;
  }
  
  public static TcpEvents of(TcpChannel channel, UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> er, ChannelFuture cf) {
    return new TcpEvents(channel, op, er, cf);
  }
  
  public static TcpEvents of(TcpChannel channel, UnaryOperator<ChannelFuture> op, ChannelFuture cf) {
    return new TcpEvents(channel, op, DEFAULT_ERROR_HANDLER, cf);
  }
  
  public static TcpEvents of(TcpChannel channel, UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> er) {
    return new TcpEvents(channel, op, er, null);
  }
  
  public static TcpEvents of(TcpChannel channel, UnaryOperator<ChannelFuture> op) {
    return new TcpEvents(channel, op, DEFAULT_ERROR_HANDLER, null);
  }
  
  public static TcpEvents of(TcpChannel channel, ChannelFuture cf) {
    return new TcpEvents(channel, null, DEFAULT_ERROR_HANDLER, cf);
  }
  
  public static TcpEvents of(TcpChannel channel) {
    return new TcpEvents(channel, null, DEFAULT_ERROR_HANDLER, null);
  }
  
  public TcpChannel channel() {
    return channel;
  }
  
  public TcpEvents applyNext(UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> er) {
    TcpEvents next = TcpEvents.of(channel, Objects.requireNonNull(op), er);
    this.addListener(next);
    if(future != null) {
      future.addListener(completeFuture());
    }
    return next;
  }
  
  public TcpEvents applyNext(UnaryOperator<ChannelFuture> op) {
    return applyNext(op, errorHandler);
  }
  
  public TcpEvents onComplete(Consumer<ChannelFuture> cs, BiConsumer<ChannelFuture,Throwable> er) {
    return applyNext(f->{cs.accept(f); return f;}, er);
  }
  
  public TcpEvents onComplete(Consumer<ChannelFuture> cs) {
    return onComplete(cs, errorHandler);
  }
  
  public TcpEvents closeFuture() {
    return channel.closeFuture();
  }
  
  public TcpEvents close() {
    return applyNext(f->f.channel().close());
  }
  
  public TcpEvents write(Object o) {
    TcpEvents e = TcpEvents.of(channel);
    TcpEvents w = TcpEvents.of(channel, f->{
      //System.out.printf("[proxyEvent] event=%s, writed=%s%n", this, o); 
      ChannelFuture ff = f.channel().write(o);
      e.setSuccess(ff);
      return ff;
    }, errorHandler);
    this.addListener(w);
    if(future != null) {
      future.addListener(completeFuture());
    }
    return e;
  }
  
  public TcpEvents writeAndFlush(Object o) {
    return applyNext(f->f.channel().writeAndFlush(o));
  }
  
  public TcpEvents flush() {
    return onComplete(f->f.channel().flush());
  }
  
  @Override
  public TcpEvents sync() {
    CountDownLatch count = new CountDownLatch(1);
    TcpEvents e = onComplete(f->Unchecked.call(()->count.countDown()));
    Unchecked.call(()->count.await());
    return e;
  }
  
  public TcpEvents sync(long time, TimeUnit unit) {
    CountDownLatch count = new CountDownLatch(1);
    TcpEvents e = onComplete(f->Unchecked.call(()->count.countDown()));
    Unchecked.call(()->count.await(time, unit));
    return e;
  }
  
  public TcpEvents shutdown() {
    return close().onComplete(f->executor().parent().shutdownGracefully());
  }


  public TcpEvents awaitShutdown() {
    executor().parent().terminationFuture().syncUninterruptibly();
    return this;
  }
  
  private GenericFutureListener<ChannelFuture> completeFuture() {
    return (ChannelFuture f) -> {
      //System.out.printf("[ProxyEvent.completeFuture] event=%s%n", this);
      if(!this.isDone()) {
        if(f.isSuccess()) this.setSuccess(f);
        else this.setFailure(f.cause());
      }
    };
  }
  
  @Override
  public void operationComplete(Future<ChannelFuture> f) throws Exception {
    //System.out.printf("[ProxyEvent.operationComplete] event=%s, future=%s, operator=%s%n", this, f, operator);
    if(f.isSuccess() && operator != null) {
      //System.out.printf("[ProxyEvent.applyOperator] operator=%s%n", operator);
      operator.apply(f.get()).addListener(completeFuture());
    }
    else if(f.cause() != null && errorHandler != null) {
      errorHandler.accept(f.get(), f.get().cause());
      this.setFailure(f.get().cause());
    }
  }

}
