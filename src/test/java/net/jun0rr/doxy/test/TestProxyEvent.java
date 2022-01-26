/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import net.jun0rr.doxy.proxy.CompletedFuture;
import net.jun0rr.doxy.proxy.FutureEvent;
import org.junit.jupiter.api.Test;
import us.pserver.tools.Sleeper;
import us.pserver.tools.date.SimpleDate;

/**
 *
 * @author juno
 */
public class TestProxyEvent {
  
  @Test
  public void completed_future() throws InterruptedException, ExecutionException, TimeoutException {
    CompletedFuture cf = new CompletedFuture(new DefaultEventExecutor());
    cf.addListener(f->{
      System.out.println("[completed_future()] Completed at " + SimpleDate.now());
    });
    cf.get();
    Sleeper.of(500).sleep();
  }
  
  @Test
  public void future_event() throws InterruptedException, ExecutionException, TimeoutException {
    try {
      EventExecutor ex = new DefaultEventExecutor();
      CompletedFuture cf = new CompletedFuture(ex);
      FutureEvent pe = FutureEvent.of(ex, cf);
      pe.onComplete(f->{System.out.println("[future_event()] Future Completed 1!");})
          .onComplete(f->{System.out.println("[future_event()] Future Completed 2!");})
          .onComplete(f->{System.out.println("[future_event()] Future Completed 3!");});
      pe.get();
      Sleeper.of(500).sleep();
    }
    catch(Throwable th) {
      th.printStackTrace();
    }
  }
  
}
