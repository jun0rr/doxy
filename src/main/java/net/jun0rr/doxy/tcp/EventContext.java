/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.concurrent.Future;
import java.util.Queue;

/**
 *
 * @author juno
 */
public interface EventContext {
  
  public Future future();
  
  public EventContext future(Future f);
  
  public Queue messages();
  
  public EventContext write(Object msg);
  
  public TcpChannel channel();
  
}
