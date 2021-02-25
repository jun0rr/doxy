/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

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
public class TcpEventChain implements EventChain {
  
  private final Event event;
  
  protected final Queue<TcpEventListener> events;
  
  
  public TcpEventChain(Event e) {
    this.event = Objects.requireNonNull(e, "Bad null Event");
    this.events = new ConcurrentLinkedQueue();
  }
  
  public TcpEventChain(TcpChannel ch) {
    this(new TcpEvent(ch));
  }
  
  @Override
  public TcpChannel channel() {
    return event.channel();
  }
  
  @Override
  public EventChain onComplete(Consumer<Event> success) {
    return onComplete(success, Unchecked::unchecked);
  }


  @Override
  public EventChain onComplete(Consumer<Event> success, Consumer<Throwable> error) {
    if(success != null && error != null) events.offer(c->{
      if(c.future().isDone()) {
        if(c.future().isSuccess()) success.accept(c);
        else error.accept(c.future().cause());
      }
      else {
        c.future().addListener(f->success.accept(c));
      }
      return c.future();
    });
    return this;
  }


  @Override
  public EventChain executeSync() {
    CountDownLatch count = new CountDownLatch(1);
    events.offer(c->{
      count.countDown();
      return c.future();
    });
    execute();
    Unchecked.call(()->count.await());
    return this;
  }


  @Override
  public EventChain close() {
    events.offer(e->e.channel().nettyChannel().close());
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
  
  private GenericFutureListener listener() {
    return f->{
      TcpEventListener el = events.poll();
      if(el != null) {
        TcpEvent evt = new TcpEvent(event.channel(), f);
        event.future(Unchecked.call(()->
            el.apply(evt)).addListener(listener())
        );
      }
    };
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
  
  @Override
  public EventChain write(Object msg) {
    events.offer(e->e.channel().nettyChannel().write(msg));
    return this;
  }

}
