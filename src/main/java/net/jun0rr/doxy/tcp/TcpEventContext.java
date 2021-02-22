/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author juno
 */
public class TcpEventContext implements EventContext {
  
  private volatile Future future;
  
  private final TcpChannel channel;
  
  private final Queue<Object> messages;
  
  private final EventLoop loop;
  
  public TcpEventContext(TcpChannel ch, Future f, EventLoop el) {
    this.channel = Objects.requireNonNull(ch, "Bad null TcpChannel");
    this.messages = new ConcurrentLinkedQueue();
    this.future = f;
    this.loop = el;
  }
  
  public TcpEventContext(TcpChannel ch, EventLoop el) {
    this(ch, null, el);
  }
  
  public TcpEventContext(TcpChannel ch, Future f) {
    this(ch, f, null);
  }
  
  public TcpEventContext(TcpChannel ch) {
    this(ch, null, null);
  }
  
  @Override
  public Future future() {
    return future;
  }

  @Override
  public EventContext future(Future f) {
    future = f;
    return this;
  }

  @Override
  public Queue messages() {
    return (loop != null)
        ? new LinkedList(messages)
        : messages;
  }

  @Override
  public EventContext write(Object msg) {
    if(loop != null) {
      future = new DefaultPromise(loop);
    }
    else if(future instanceof ChannelFuture) {
      future = ((ChannelFuture)future).channel().newPromise();
    }
    messages.offer(msg);
    return this;
  }

  @Override
  public TcpChannel channel() {
    return channel;
  }
  
}
