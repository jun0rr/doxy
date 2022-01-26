/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import java.util.Objects;

/**
 *
 * @author juno
 */
public class ChannelErrorContext {
  
  private final ChannelHandlerContext ctx;
  
  private final InternalLogger logger;
  
  private final Throwable error;
  
  public ChannelErrorContext(ChannelHandlerContext ctx, InternalLogger logger, Throwable error) {
    this.ctx = Objects.requireNonNull(ctx);
    this.logger = Objects.requireNonNull(logger);
    this.error = Objects.requireNonNull(error);
  }
  
  public ChannelHandlerContext context() {
    return ctx;
  }
  
  public InternalLogger logger() {
    return logger;
  }
  
  public Throwable error() {
    return error;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.ctx);
    hash = 67 * hash + Objects.hashCode(this.error);
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
    final ChannelErrorContext other = (ChannelErrorContext) obj;
    if (!Objects.equals(this.ctx, other.ctx)) {
      return false;
    }
    if (!Objects.equals(this.error, other.error)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ChannelErrorContext{" + "ctx=" + ctx + ", error=" + error + '}';
  }
  
}
