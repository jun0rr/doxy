/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class ContextEventChain implements EventChain {
  
  private final ChannelHandlerContext context;
  
  private final Event event;
  
  protected final Queue<TcpEventListener> events;
  
  protected final ChannelPromise promise;
  
  public ContextEventChain(Event evt, ChannelHandlerContext ctx, ChannelPromise cp) {
    this.event = Objects.requireNonNull(evt, "Bad null EventContext");
    this.context = Objects.requireNonNull(ctx, "Bad null ChannelHandlerContext");
    this.events = new ConcurrentLinkedQueue();
    this.promise = cp;
  }
  
  public ContextEventChain(Event evt, ChannelHandlerContext ctx) {
    this(evt, ctx, null);
  }
  
  @Override
  public TcpChannel channel() {
    return event.channel();
  }
  
  @Override
  public EventChain write(Object msg) {
    events.offer((promise != null) 
        ? e->context.writeAndFlush(msg, promise) 
        : e->context.writeAndFlush(msg)
    );
    return this;
  }
  
  @Override
  public EventChain onComplete(Consumer<Event> success) {
    return onComplete(success, Unchecked::unchecked);
  }
  
  @Override
  public EventChain onComplete(Consumer<Event> success, Consumer<Throwable> error) {
    if(success != null && error != null) events.offer(e->{
      if(e.future().isDone()) {
        if(e.future().isSuccess()) success.accept(e);
        else error.accept(e.future().cause());
      }
      return e.future();
    });
    return this;
  }
  
  @Override
  public EventChain close() {
    events.offer(e->(context.channel().isOpen()) ? context.channel().close() : e.future());
    return this;
  }
  
  @Override
  public EventChain shutdown() {
    close();
    events.offer(e->e.channel().group().shutdownGracefully());
    return this;
  }
  
  @Override
  public EventChain awaitShutdown() {
    event.channel().group().terminationFuture().syncUninterruptibly();
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

  @Override
  public EventChain execute() {
    if(event.channel().group().isShutdown() || event.channel().group().isTerminated()) {
      TcpEventListener el;
      while((el = events.poll()) != null) {
        try {
          event.future(el.apply(event));
        } catch (Exception ex) {
          Unchecked.unchecked(ex);
        }
      }
    }
    else {
      event.future().addListener(listener());
    }
    return this;
  }
  
  private GenericFutureListener listener() {
    return f->Optional.ofNullable(events.poll())
        .map(l->Unchecked.call(()->l.apply(event)))
        .ifPresent(event::future);
  }
  
}
