/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.util.ReferenceCounted;
import java.util.Optional;
import net.jun0rr.doxy.common.MessageContainer;


/**
 *
 * @author Juno
 */
public interface TcpExchange extends MessageContainer {
  
  public TcpSession session();
  
  public TcpChannel channel();
  
  public TcpChannel bootstrapChannel(); 
  
  /**
   * Send the message (aborting the inbound pipeline).
   * @return Emtpy Optional.
   */
  public Optional<? extends TcpExchange> send();
  
  public Optional<? extends TcpExchange> sendAndFlush();
  
  public Optional<? extends TcpExchange> sendAndClose();
  
  /**
   * Return an empty optional.
   * @return Emtpy Optional.
   */
  @Override
  public Optional<? extends TcpExchange> empty();
  
  /**
   * Return a TcpExchange without message.
   * @return TcpExchange without message.
   */
  @Override
  public Optional<? extends TcpExchange> forward();
  
  /**
   * Return a TcpExchange with the new message.
   * @param msg New message.
   * @return TcpExchange with new message.
   */
  @Override
  public TcpExchange message(Object msg);
  
  
  
  public static TcpExchange of(TcpChannel boot, ConnectedTcpChannel channel, Object msg) {
    return new TcpExchangeImpl(boot, channel, msg);
  }
  
  public static TcpExchange of(TcpChannel boot, ConnectedTcpChannel channel) {
    return new TcpExchangeImpl(boot, channel);
  }
  
  
  
  
  
  static class TcpExchangeImpl implements TcpExchange {
    
    protected final TcpChannel boot;
    
    protected final ConnectedTcpChannel connected;
    
    protected final Object message;
    
    public TcpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, Object msg) {
      this.boot = boot;
      this.connected = connected;
      this.message = msg;
    }
    
    public TcpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected) {
      this(boot, connected, null);
    }
    
    @Override
    public TcpChannel channel() {
      return connected;
    }
    
    @Override
    public TcpSession session() {
      return connected.session();
    }
    
    @Override
    public TcpChannel bootstrapChannel() {
      return boot;
    }
    
    @Override
    public <T> T message() {
      return (T) message;
    }
    
    @Override
    public TcpExchange message(Object msg) {
      if(message != null && message != msg && message instanceof ReferenceCounted) {
        ReferenceCounted r = (ReferenceCounted) message;
        if(r.refCnt() > 0) r.release(r.refCnt());
      }
      return new TcpExchangeImpl(boot, connected, msg);
    }
    
    @Override
    public Optional<? extends TcpExchange> send() {
      connected.events().write(message);
      return empty();
    }
    
    @Override
    public Optional<? extends TcpExchange> sendAndFlush() {
      connected.events().writeAndFlush(message);
      return empty();
    }
    
    @Override
    public Optional<? extends TcpExchange> sendAndClose() {
      connected.events().writeAndFlush(message).close();
      return empty();
    }
    
    @Override
    public Optional<? extends TcpExchange> empty() {
      return Optional.empty();
    }


    @Override
    public Optional<? extends TcpExchange> forward() {
      return Optional.of(this);
    }
    
  }
  
}
