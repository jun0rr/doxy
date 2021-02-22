/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;
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
public class TcpEventChain implements EventChain {
  
  protected final Event context;
  
  protected final List<TcpEventListener> events;
  
  protected final ChannelPromise promise;
  
  
  public TcpEventChain(Event ctx, ChannelPromise cp) {
    this.context = Objects.requireNonNull(ctx, "Bad null EventContext");
    this.events = new LinkedList<>();
    this.promise = cp;
  }
  
  public TcpEventChain(Event ctx) {
    this(ctx, null);
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
    events.add(c->{
      count.countDown();
      return c.future();
    });
    execute();
    Unchecked.call(()->count.await());
    return this;
  }


  @Override
  public EventChain close() {
    events.add(c->c.channel().nettyChannel().close());
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
    context.channel().group().terminationFuture().syncUninterruptibly();
    return this;
  }
  
  private GenericFutureListener listener(Iterator<TcpEventListener> it) {
    return f -> {
      if(it.hasNext()) {
        context.future(it.next().apply(context));
        execute(it);
      }
    };
  }
  
  private void execute(Iterator<TcpEventListener> it) {
    if(context.channel().group().isShutdown() || context.channel().group().isTerminated()) {
      while(it.hasNext()) {
        context.future(Unchecked.call(()->it.next().apply(context)));
      }
    }
    else {
      context.future().addListener(listener(it));
    }
  }
  
  @Override
  public EventChain write(Object msg) {
    events.add(c->{
      Event e = c;
      ChannelPromise cp = e.channel().nettyChannel().newPromise();
      System.out.println("[--TcpEventChain.write--] Promise=" + cp);
      GenericFutureListener fl = f->{
        System.out.println("[--TcpEventChain.write--] Promise Executed! " + f);
        context.future(f);
        execute();
      };
      cp.addListener(fl);
      return c.channel().nettyChannel().writeAndFlush(msg).addListener(fl);
    });
    return this;
  }

  @Override
  public EventChain execute() {
    execute(events.iterator());
    return this;
  }

  @Override
  public Event context() {
    return context;
  }
  
}
