/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.concurrent.Future;
import java.util.Objects;
import java.util.function.Consumer;


/**
 *
 * @author Juno
 */
public class TcpServerEventContext implements EventContext {
  
  private final EventContext context;
  
  public TcpServerEventContext(EventContext chain) {
    this.context = Objects.requireNonNull(chain, "Bad null TcpEventChain");
  }
  
  @Override
  public EventContext onComplete(Consumer<Event> success) {
    context.onComplete(success);
    return this;
  }


  @Override
  public EventContext onComplete(Consumer<Event> success, Consumer<Throwable> error) {
    context.onComplete(success, error);
    return this;
  }


  @Override
  public EventContext executeSync() {
    context.executeSync();
    return this;
  }


  @Override
  public EventContext close() {
    context.close();
    return this;
  }


  @Override
  public EventContext shutdown() {
    context.shutdown();
    return this;
  }


  @Override
  public EventContext awaitShutdown() {
    context.awaitShutdown();
    return this;
  }
  
  @Override
  public EventContext write(Object msg) {
    throw new UnsupportedOperationException("Cannot write on server TcpChannel");
  }

  @Override
  public EventContext execute() {
    context.execute();
    return this;
  }

  @Override
  public Future future() {
    return context.future();
  }

  @Override
  public EventContext future(Future f) {
    context.future(f);
    return this;
  }

  @Override
  public TcpChannel channel() {
    return context.channel();
  }

}
