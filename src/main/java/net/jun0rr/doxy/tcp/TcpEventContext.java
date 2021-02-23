/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.concurrent.Future;
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
public class TcpEventContext implements EventContext {
  
  private final TcpChannel channel;
  
  private volatile Future future;
  
  protected final Queue<TcpEventListener> events;
  
  
  public TcpEventContext(TcpEvent e) {
    this.channel = Objects.requireNonNull(e, "Bad null TcpChannel").channel();
    this.events = new ConcurrentLinkedQueue();
    this.future = e.future();
  }
  
  public TcpEventContext(TcpChannel ch) {
    this(new TcpEvent(ch));
  }
  
  @Override
  public Future future() {
    return future;
  }
  
  @Override
  public TcpEventContext future(Future f) {
    future = f;
    if(f != null && !events.isEmpty()) execute();
    return this;
  }
  
  @Override
  public TcpChannel channel() {
    return channel;
  }
  
  @Override
  public EventContext onComplete(Consumer<Event> success) {
    return onComplete(success, Unchecked::unchecked);
  }


  @Override
  public EventContext onComplete(Consumer<Event> success, Consumer<Throwable> error) {
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
  public EventContext executeSync() {
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
  public EventContext close() {
    events.add(c->c.channel().nettyChannel().close());
    return this;
  }


  @Override
  public EventContext shutdown() {
    close();
    events.add(c->c.channel().group().shutdownGracefully());
    return this;
  }


  @Override
  public EventContext awaitShutdown() {
    channel.group().terminationFuture().syncUninterruptibly();
    return this;
  }
  
  private GenericFutureListener listener() {
    return f->Optional.ofNullable(events.poll())
        .map(l->Unchecked.call(()->
            l.apply(new TcpEvent(channel, f))))
        .ifPresent(this::future);
  }
  
  @Override
  public EventContext execute() {
    if(channel.group().isShutdown() || channel.group().isTerminated()) {
      TcpEventListener el;
      while((el = events.poll()) != null) {
        try {
          future(el.apply(new TcpEvent(channel, future)));
        } catch (Exception ex) {
          Unchecked.unchecked(ex);
        }
      }
    }
    else {
      future().addListener(listener());
    }
    return this;
  }
  
  @Override
  public EventContext write(Object msg) {
    events.add(e->e.channel().nettyChannel().write(msg));
    //events.add(c->{
      //Event e = c;
      //ChannelPromise cp = e.channel().nettyChannel().newPromise();
      //System.out.println("[--TcpEventChain.write--] Promise=" + cp);
      //GenericFutureListener fl = f->{
        //System.out.println("[--TcpEventChain.write--] Promise Executed! " + f);
        //context.future(f);
        //execute();
      //};
      //cp.addListener(fl);
      //return c.channel().nettyChannel().writeAndFlush(msg).addListener(fl);
    //});
    return this;
  }

}
