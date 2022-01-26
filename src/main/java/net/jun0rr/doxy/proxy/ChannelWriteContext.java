/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.Objects;

/**
 *
 * @author juno
 */
public class ChannelWriteContext {
  
  private final ChannelHandlerContext ctx;
  
  private final Object message;
  
  private final ChannelPromise pms;
  
  public ChannelWriteContext(ChannelHandlerContext ctx, Object message, ChannelPromise pms) {
    this.ctx = Objects.requireNonNull(ctx);
    this.message = Objects.requireNonNull(message);
    this.pms = Objects.requireNonNull(pms);
  }
  
  public ChannelHandlerContext context() {
    return ctx;
  }
  
  public Object message() {
    return message;
  }
  
  public ChannelPromise promise() {
    return pms;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.ctx);
    hash = 67 * hash + Objects.hashCode(this.message);
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
    final ChannelWriteContext other = (ChannelWriteContext) obj;
    if (!Objects.equals(this.ctx, other.ctx)) {
      return false;
    }
    if (!Objects.equals(this.message, other.message)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ChannelErrorContext{" + "ctx=" + ctx + ", message=" + message + '}';
  }
  
}
