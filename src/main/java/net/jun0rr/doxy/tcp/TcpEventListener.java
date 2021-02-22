/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.concurrent.Future;
import java.util.EventListener;


/**
 *
 * @author juno
 */
public interface TcpEventListener extends EventListener {
  
  public Future apply(Event ctx) throws Exception;
  
}
