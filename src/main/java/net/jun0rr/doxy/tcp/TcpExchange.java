/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCounted;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import net.jun0rr.doxy.common.MessageContainer;


/**
 *
 * @author Juno
 */
public interface TcpExchange extends MessageContainer {
  
  public Map<String,Object> attributes();
  
  public TcpExchange setAttr(String key, Object val);
  
  public <A> Optional<A> getAttr(String key);
  
  public TcpChannel channel();
  
  public TcpChannel bootstrapChannel(); 
  
  /**
   * Send the message (aborting the inbound pipeline).
   * @return Emtpy Optional.
   */
  public Optional<? extends TcpExchange> send();
  
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
  public TcpExchange withMessage(Object msg);
  
  
  
  public static TcpExchange of(TcpChannel boot, ConnectedTcpChannel channel, Object msg) {
    return new TcpExchangeImpl(boot, channel, new TreeMap<>(), msg);
  }
  
  public static TcpExchange of(TcpChannel boot, ConnectedTcpChannel channel) {
    return new TcpExchangeImpl(boot, channel, new TreeMap<>());
  }
  
  
  
  
  
  static class TcpExchangeImpl implements TcpExchange {
    
    protected final TcpChannel boot;
    
    protected final ConnectedTcpChannel connected;
    
    protected final Map<String,Object> attributes;
    
    protected final Object message;
    
    public TcpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, Map<String,Object> attrs, Object msg) {
      this.boot = boot;
      this.connected = connected;
      this.attributes = attrs;
      this.message = msg;
    }
    
    public TcpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, Map<String,Object> attrs) {
      this(boot, connected, attrs, null);
    }
    
    @Override
    public TcpChannel channel() {
      return connected;
    }
    
    @Override
    public TcpChannel bootstrapChannel() {
      return boot;
    }
    
    @Override
    public Map<String, Object> attributes() {
      return attributes;
    }
    
    @Override
    public TcpExchange setAttr(String key, Object val) {
      if(key != null && val != null) {
        attributes.put(key, val);
      }
      return this;
    }
    
    @Override
    public <T> Optional<T> getAttr(String key) {
      return Optional.ofNullable((T)attributes.get(key));
    }
    
    @Override
    public <T> T message() {
      return (T) message;
    }
    
    @Override
    public TcpExchange withMessage(Object msg) {
      if(message != null && message != msg && message instanceof ReferenceCounted) {
        ReferenceCounted r = (ReferenceCounted) message;
        if(r.refCnt() > 0) r.release(r.refCnt());
      }
      return new TcpExchangeImpl(boot, connected, attributes, msg);
    }
    
    @Override
    public Optional<? extends TcpExchange> send() {
      connected.eventChain().write(message).execute();
      return empty();
    }
    
    @Override
    public Optional<? extends TcpExchange> sendAndClose() {
      connected.eventChain().write(message).close().execute();
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
