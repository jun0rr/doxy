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
public class TcpServerEventChain implements EventChain {
  
  private final EventChain chain;
  
  public TcpServerEventChain(EventChain chain) {
    this.chain = Objects.requireNonNull(chain, "Bad null TcpEventChain");
  }
  
  @Override
  public EventChain onComplete(Consumer<Event> success) {
    chain.onComplete(success);
    return this;
  }


  @Override
  public EventChain onComplete(Consumer<Event> success, Consumer<Throwable> error) {
    chain.onComplete(success, error);
    return this;
  }


  @Override
  public EventChain executeSync() {
    chain.executeSync();
    return this;
  }


  @Override
  public EventChain close() {
    chain.close();
    return this;
  }


  @Override
  public EventChain shutdown() {
    chain.shutdown();
    return this;
  }


  @Override
  public EventChain awaitShutdown() {
    chain.awaitShutdown();
    return this;
  }
  
  @Override
  public EventChain write(Object msg) {
    throw new UnsupportedOperationException("Cannot write on server TcpChannel");
  }

  @Override
  public EventChain execute() {
    chain.execute();
    return this;
  }

  @Override
  public TcpChannel channel() {
    return chain.channel();
  }

}
