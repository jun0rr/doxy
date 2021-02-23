/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.concurrent.Future;
import java.util.function.Consumer;


/**
 *
 * @author Juno
 */
public interface EventContext {
  
  public EventContext onComplete(Consumer<Event> success);
  
  public EventContext onComplete(Consumer<Event> success, Consumer<Throwable> error);
  
  public EventContext execute();
  
  public EventContext executeSync();
  
  public EventContext close();
  
  public EventContext shutdown();
  
  public EventContext awaitShutdown();
  
  public EventContext write(Object obj);
  
  public Future future();
  
  public EventContext future(Future f);
  
  public TcpChannel channel();
  
}
