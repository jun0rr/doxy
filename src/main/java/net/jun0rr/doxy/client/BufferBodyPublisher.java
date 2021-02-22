/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.client;

import java.net.http.HttpRequest.BodyPublisher;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.Flow;


/**
 *
 * @author juno
 */
public class BufferBodyPublisher implements BodyPublisher {
  
  private final Deque<ByteBuffer> buffers;
  
  public BufferBodyPublisher(ByteBuffer... bufs) {
    buffers = new ArrayDeque<>(Arrays.asList(bufs));
  }
  
  @Override
  public long contentLength() {
    return buffers.stream().mapToInt(ByteBuffer::remaining).sum();
  }

  @Override
  public void subscribe(Flow.Subscriber<? super ByteBuffer> subs) {
    subs.onSubscribe(new BufferSubscription(subs));
  }
  
  
  
  private class BufferSubscription implements Flow.Subscription {
    
    private final Flow.Subscriber<? super ByteBuffer> subs;
    
    private volatile boolean canceled;
    
    public BufferSubscription(Flow.Subscriber<? super ByteBuffer> s) {
      this.subs = Objects.requireNonNull(s, "Bad null Flow.Subscriber<ByteBuffer> (s)");
      this.canceled = false;
    }
    
    @Override
    public void request(long n) {
      for(int i = 0; i < n && !canceled; i++) {
        ByteBuffer b = buffers.pollFirst();
        if(b == null) subs.onComplete();
        else subs.onNext(b);
      }
    }
    
    @Override
    public void cancel() {
      canceled = true;
    }
    
  }
  
}
