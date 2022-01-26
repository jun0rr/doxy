/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 *
 * @author juno
 */
public class FutureEvent extends DefaultPromise<Void> implements GenericFutureListener {
  
  private final Future future;
  
  private final UnaryOperator<Future> operator;
  
  private final AtomicBoolean applied;
  
  private FutureEvent(EventExecutor ex, UnaryOperator<Future> op, Future cf) {
    super(ex);
    this.operator = op;
    this.future = cf;
    this.applied = new AtomicBoolean(false);
  }
  
  public static FutureEvent of(EventExecutor ex, UnaryOperator<Future> op, Future cf) {
    return new FutureEvent(ex, op, cf);
  }
  
  public static FutureEvent of(EventExecutor ex, UnaryOperator<Future> op) {
    return new FutureEvent(ex, op, null);
  }
  
  public static FutureEvent of(EventExecutor ex, Future cf) {
    return new FutureEvent(ex, null, cf);
  }
  
  public static FutureEvent of(EventExecutor ex) {
    return new FutureEvent(ex, null, null);
  }
  
  public FutureEvent onComplete(UnaryOperator<Future> op) {
    FutureEvent next = FutureEvent.of(executor(), Objects.requireNonNull(op));
    this.addListener(next);
    if(future != null) {
      future.addListener(this);
    }
    return next;
  }
  
  public FutureEvent onComplete(Consumer<Future> cs) {
    return onComplete(f->{cs.accept(f); return f;});
  }
  
  @Override
  public void operationComplete(Future f) throws Exception {
    if(operator != null && applied.compareAndSet(false, true)) {
      operator.apply(f).addListener(this);
    }
    else if(f.isSuccess()) {
      this.setSuccess(null);
    }
    else {
      this.setFailure(f.cause());
    }
  }

}
