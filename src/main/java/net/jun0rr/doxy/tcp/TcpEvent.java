/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.concurrent.Future;
import java.util.Objects;

/**
 *
 * @author juno
 */
public class TcpEvent implements Event {
  
  private volatile Future future;
  
  private final TcpChannel channel;
  
  public TcpEvent(TcpChannel ch, Future f) {
    this.channel = Objects.requireNonNull(ch, "Bad null TcpChannel");
    this.future = f;
  }
  
  public TcpEvent(TcpChannel ch) {
    this(ch, null);
  }
  
  @Override
  public Future future() {
    return future;
  }

  @Override
  public Event future(Future f) {
    future = f;
    return this;
  }

  @Override
  public TcpChannel channel() {
    return channel;
  }
  
}
