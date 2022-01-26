/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 *
 * @author juno
 */
public class ProxyEvent extends DefaultPromise<ChannelFuture> implements GenericFutureListener<Future<ChannelFuture>> {
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyEvent.class);
  
  public static final BiConsumer<ChannelFuture,Throwable> DEFAULT_ERROR_HANDLER = (c,e)->logger.error(e);
  
  private final ChannelFuture future;
  
  private final UnaryOperator<ChannelFuture> operator;
  
  private final BiConsumer<ChannelFuture,Throwable> errorHandler;
  
  private ProxyEvent(EventExecutor ex, UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> errorHandler, ChannelFuture cf) {
    super(ex);
    this.operator = op;
    this.errorHandler = errorHandler;
    this.future = cf;
  }
  
  public static ProxyEvent of(EventExecutor ex, UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> er, ChannelFuture cf) {
    return new ProxyEvent(ex, op, er, cf);
  }
  
  public static ProxyEvent of(EventExecutor ex, UnaryOperator<ChannelFuture> op, ChannelFuture cf) {
    return new ProxyEvent(ex, op, DEFAULT_ERROR_HANDLER, cf);
  }
  
  public static ProxyEvent of(EventExecutor ex, UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> er) {
    return new ProxyEvent(ex, op, er, null);
  }
  
  public static ProxyEvent of(EventExecutor ex, UnaryOperator<ChannelFuture> op) {
    return new ProxyEvent(ex, op, DEFAULT_ERROR_HANDLER, null);
  }
  
  public static ProxyEvent of(ChannelFuture cf) {
    return new ProxyEvent(cf.channel().eventLoop(), null, DEFAULT_ERROR_HANDLER, cf);
  }
  
  public static ProxyEvent of(EventExecutor ex) {
    return new ProxyEvent(ex, null, DEFAULT_ERROR_HANDLER, null);
  }
  
  
  public ProxyEvent applyNext(UnaryOperator<ChannelFuture> op, BiConsumer<ChannelFuture,Throwable> er) {
    ProxyEvent next = ProxyEvent.of(executor(), Objects.requireNonNull(op), er);
    this.addListener(next);
    if(future != null) {
      future.addListener(completeFuture());
    }
    return next;
  }
  
  public ProxyEvent applyNext(UnaryOperator<ChannelFuture> op) {
    return applyNext(op, errorHandler);
  }
  
  public ProxyEvent onComplete(Consumer<ChannelFuture> cs, BiConsumer<ChannelFuture,Throwable> er) {
    return applyNext(f->{cs.accept(f); return f;}, er);
  }
  
  public ProxyEvent onComplete(Consumer<ChannelFuture> cs) {
    return onComplete(cs, errorHandler);
  }
  
  public ProxyEvent onClose(Consumer<ChannelFuture> cs, BiConsumer<ChannelFuture,Throwable> er) {
    ProxyEvent e = ProxyEvent.of(executor());
    applyNext(f->f.channel().closeFuture().addListener(e.completeFuture()));
    return e.onComplete(cs, er);
  }
  
  public ProxyEvent onClose(Consumer<ChannelFuture> cs) {
    return onClose(cs, errorHandler);
  }
  
  public ProxyEvent close() {
    return applyNext(f->f.channel().close());
  }
  
  public ProxyEvent write(Object o) {
    ProxyEvent e = ProxyEvent.of(executor());
    ProxyEvent w = ProxyEvent.of(executor(), f->{
      //System.out.printf("[proxyEvent] event=%s, writed=%s%n", this, o); 
      e.setSuccess(f);
      return f.channel().write(o);
    }, errorHandler);
    this.addListener(w);
    if(future != null) {
      future.addListener(completeFuture());
    }
    return e;
  }
  
  public ProxyEvent writeAndFlush(Object o) {
    return applyNext(f->f.channel().writeAndFlush(o));
  }
  
  public ProxyEvent flush() {
    return onComplete(f->f.channel().flush());
  }
  
  private GenericFutureListener<ChannelFuture> completeFuture() {
    return (ChannelFuture f) -> {
      //System.out.printf("[ProxyEvent.completeFuture] event=%s%n", this);
      if(f.isSuccess()) this.setSuccess(f);
      else this.setFailure(f.cause());
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
