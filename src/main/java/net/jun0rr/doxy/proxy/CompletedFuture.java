/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;

/**
 *
 * @author juno
 */
public class CompletedFuture extends CompleteFuture {

  public CompletedFuture(EventExecutor ex) {
    super(ex);
  }
  
  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public Throwable cause() {
    return null;
  }

  @Override
  public Object getNow() {
    return null;
  }
  
}
