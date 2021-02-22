/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class ContextEventChain implements EventChain {
  
  private final ChannelHandlerContext context;
  
  private final Event econtext;
  
  protected final List<TcpEventListener> events;
  
  protected final ChannelPromise promise;
  
  public ContextEventChain(Event ecx, ChannelHandlerContext ctx, ChannelPromise cp) {
    this.econtext = Objects.requireNonNull(ecx, "Bad null EventContext");
    this.context = Objects.requireNonNull(ctx, "Bad null ChannelHandlerContext");
    this.events = new LinkedList<>();
    this.promise = cp;
  }
  
  public ContextEventChain(Event ectx, ChannelHandlerContext ctx) {
    this(ectx, ctx, null);
  }
  
  @Override
  public EventChain write(Object msg) {
    events.add((promise != null) 
        ? c->context.writeAndFlush(msg, promise) 
        : c->context.writeAndFlush(msg)
    );
    return this;
  }
  
  @Override
  public EventChain onComplete(Consumer<Event> success) {
    return onComplete(success, Unchecked::unchecked);
  }
  
  @Override
  public EventChain onComplete(Consumer<Event> success, Consumer<Throwable> error) {
    if(success != null && error != null) events.add(c->{
      if(c.future().isDone()) {
        if(c.future().isSuccess()) success.accept(c);
        else error.accept(c.future().cause());
      }
      return c.future();
    });
    return this;
  }
  
  @Override
  public EventChain close() {
    events.add(c->(context.channel().isOpen()) ? context.channel().close() : c.future());
    return this;
  }
  
  @Override
  public EventChain shutdown() {
    close();
    events.add(c->c.channel().group().shutdownGracefully());
    return this;
  }
  
  @Override
  public EventChain awaitShutdown() {
    econtext.channel().group().terminationFuture().syncUninterruptibly();
    return this;
  }
  
  @Override
  public EventChain execute() {
    execute(Collections.unmodifiableList(events).iterator());
    return this;
  }
  
  @Override
  public EventChain executeSync() {
    CountDownLatch count = new CountDownLatch(1);
    events.add(c->{
      count.countDown();
      return c.future();
    });
    execute();
    Unchecked.call(()->count.await());
    return this;
  }

  private void execute(Iterator<TcpEventListener> it) {
    if(!it.hasNext()) return;
    if(context.executor().isShutdown() || context.executor().isTerminated()) {
      while(it.hasNext()) {
        econtext.future(Unchecked.call(()->it.next().apply(econtext)));
      }
    }
    else {
      econtext.future().addListener(listener(it));
    }
  }
  
  private GenericFutureListener listener(Iterator<TcpEventListener> it) {
    return f -> {
      if(it.hasNext()) {
        econtext.future(it.next().apply(econtext));
        execute(it);
      }
    };
  }
  
  @Override
  public Event context() {
    return econtext;
  }

}
