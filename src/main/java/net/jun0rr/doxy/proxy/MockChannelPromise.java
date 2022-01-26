/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author juno
 */
public class MockChannelPromise implements ChannelFuture {
  
  private final ChannelFuture future;
  
  public MockChannelPromise(ChannelFuture f) {
    this.future = Objects.requireNonNull(f);
  }

  @Override
  public Channel channel() {
    return future.channel();
  }

  @Override
  public boolean isVoid() {
    return future.isVoid();
  }

  @Override
  public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> gl) {
    future.addListener(gl);
    return this;
  }

  @Override
  public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... gls) {
    for(int i = 0; i < gls.length; i++) {
      future.addListener(gls[i]);
    }
    return this;
  }

  @Override
  public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> gl) {
    future.removeListener(gl);
    return this;
  }

  @Override
  public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... gls) {
    for(int i = 0; i < gls.length; i++) {
      future.removeListener(gls[i]);
    }
    return this;
  }

  @Override
  public ChannelFuture sync() throws InterruptedException {
    future.sync();
    return this;
  }

  @Override
  public ChannelFuture syncUninterruptibly() {
    future.syncUninterruptibly();
    return this;
  }

  @Override
  public ChannelFuture await() throws InterruptedException {
    future.await();
    return this;
  }

  @Override
  public ChannelFuture awaitUninterruptibly() {
    future.awaitUninterruptibly();
    return this;
  }

  @Override
  public boolean isSuccess() {
    return future.isSuccess();
  }

  @Override
  public boolean isCancellable() {
    return future.isCancellable();
  }

  @Override
  public Throwable cause() {
    return future.cause();
  }

  @Override
  public boolean await(long l, TimeUnit tu) throws InterruptedException {
    return future.await(l, tu);
  }

  @Override
  public boolean await(long l) throws InterruptedException {
    return future.await(l);
  }

  @Override
  public boolean awaitUninterruptibly(long l, TimeUnit tu) {
    return future.awaitUninterruptibly(l, tu);
  }

  @Override
  public boolean awaitUninterruptibly(long l) {
    return future.awaitUninterruptibly(l);
  }

  @Override
  public Void getNow() {
    return null;
  }

  @Override
  public boolean cancel(boolean bln) {
    return future.cancel(bln);
  }

  @Override
  public boolean isCancelled() {
    return future.isCancelled();
  }

  @Override
  public boolean isDone() {
    return future.isDone();
  }

  @Override
  public Void get() throws InterruptedException, ExecutionException {
    return null;
  }

  @Override
  public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return null;
  }
  
}
