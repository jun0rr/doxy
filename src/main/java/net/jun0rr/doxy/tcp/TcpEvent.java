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
public class TcpEvent<T> {
  
  private final Future future;
  
  private final T attachment;
  
  public TcpEvent(Future f, T o) {
    this.future = f;
    this.attachment = o;
  }
  
  public static <U> TcpEvent of(Future f, U u) {
    return new TcpEvent(f, u);
  }
  
  public Future future() {
    return future;
  }
  
  public TcpEvent future(Future f) {
    return of(f, attachment);
  }
  
  public T attachment() {
    return attachment;
  }
  
  public TcpEvent attachment(T o) {
    return of(future, o);
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.future);
    hash = 89 * hash + Objects.hashCode(this.attachment);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcpEvent other = (TcpEvent) obj;
    if (!Objects.equals(this.future(), other.future())) {
      return false;
    }
    if (!Objects.equals(this.attachment(), other.attachment())) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "TcpEvent{" + "future=" + future + ", attachment=" + attachment + '}';
  }
  
}
