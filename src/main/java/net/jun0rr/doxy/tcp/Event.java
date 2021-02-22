/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.concurrent.Future;

/**
 *
 * @author juno
 */
public interface Event {
  
  public Future future();
  
  public Event future(Future f);
  
  public TcpChannel channel();
  
}
