/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import java.util.Objects;
import java.util.function.Consumer;


/**
 *
 * @author Juno
 */
public class TcpServerEventChain implements EventChain {
  
  private final EventChain eventChain;
  
  public TcpServerEventChain(EventChain chain) {
    this.eventChain = Objects.requireNonNull(chain, "Bad null TcpEventChain");
  }
  
  @Override
  public EventChain onComplete(Consumer<Event> success) {
    eventChain.onComplete(success);
    return this;
  }


  @Override
  public EventChain onComplete(Consumer<Event> success, Consumer<Throwable> error) {
    eventChain.onComplete(success, error);
    return this;
  }


  @Override
  public EventChain executeSync() {
    eventChain.executeSync();
    return this;
  }


  @Override
  public EventChain close() {
    eventChain.close();
    return this;
  }


  @Override
  public EventChain shutdown() {
    eventChain.shutdown();
    return this;
  }


  @Override
  public EventChain awaitShutdown() {
    eventChain.awaitShutdown();
    return this;
  }
  
  @Override
  public EventChain write(Object msg) {
    throw new UnsupportedOperationException("Cannot write on server TcpChannel");
  }

  @Override
  public EventChain execute() {
    eventChain.execute();
    return this;
  }

  @Override
  public Event context() {
    return eventChain.context();
  }
  
}
