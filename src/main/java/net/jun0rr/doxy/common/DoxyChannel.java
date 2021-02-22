/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.channel.Channel;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import net.jun0rr.doxy.tcp.TcpChannel;
import us.pserver.tools.Unchecked;


/**
 *
 * @author juno
 */
public interface DoxyChannel extends AutoCloseable {
  
  public DoxyEnvironment environment();
  
  public String uid();
  
  public long nextOrder();
  
  public TcpChannel channel();
  
  @Override public void close();
  
  public void writePacket(Packet p);
  
  
  
  public static DoxyChannel of(DoxyEnvironment env, String channelID, TcpChannel c) {
    return new DoxyChannelImpl(env, channelID, c);
  }
  
  
  
  
  
  public class DoxyChannelImpl implements DoxyChannel {

    private final DoxyEnvironment env;

    private final String uid;

    private final AtomicLong order;

    private final TcpChannel channel;

    private final PacketDecoder decoder;

    public DoxyChannelImpl(DoxyEnvironment env, String uid, TcpChannel sc) {
      this.env = Objects.requireNonNull(env, "Bad null DoxyEnvironment");
      this.uid = Objects.requireNonNull(uid, "Bad null uid String");
      this.channel = Objects.requireNonNull(sc, "Bad null Channel");
      this.order = new AtomicLong(0L);
      this.decoder = new PacketDecoder(
          env.configuration().getSecurityConfig().getCryptAlgorithm(), 
          env.getPrivateKey()
      );
    }

    @Override
    public DoxyEnvironment environment() {
      return env;
    }

    @Override
    public String uid() {
      return uid;
    }

    @Override
    public long nextOrder() {
      return order.getAndIncrement();
    }

    @Override
    public TcpChannel channel() {
      return channel;
    }

    @Override
    public void close() {
      Unchecked.call(()->channel.close());
      env.channels().remove(this);
    }

    @Override
    public void writePacket(Packet p) {
      channel.events().write(p.data()).execute();
      //channel.write(decoder.decodePacket(p).data());
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 59 * hash + Objects.hashCode(this.uid);
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
      final DoxyChannel other = (DoxyChannel) obj;
      return Objects.equals(this.uid(), other.uid());
    }

    @Override
    public String toString() {
      return "DoxyChannel{" + "uid=" + uid + ", order=" + order + ", channel=" + channel + '}';
    }

  }

}
