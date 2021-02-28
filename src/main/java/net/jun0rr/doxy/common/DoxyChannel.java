/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.buffer.Unpooled;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import net.jun0rr.doxy.tcp.TcpChannel;


/**
 *
 * @author juno
 */
public interface DoxyChannel extends AutoCloseable {
  
  public String uid();
  
  public long nextOrder();
  
  public TcpChannel channel();
  
  @Override public void close();
  
  public void writePacketData(Packet p);
  
  
  
  public static DoxyChannel of(String channelID, TcpChannel c) {
    return new DoxyChannelImpl(channelID, c);
  }
  
  
  
  
  
  public class DoxyChannelImpl implements DoxyChannel {

    private final String uid;

    private final AtomicLong order;

    private final TcpChannel channel;

    public DoxyChannelImpl(String uid, TcpChannel sc) {
      this.uid = Objects.requireNonNull(uid, "Bad null uid String");
      this.channel = Objects.requireNonNull(sc, "Bad null Channel");
      this.order = new AtomicLong(0L);
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
      channel.eventChain().close().execute();
    }

    @Override
    public void writePacketData(Packet p) {
      channel.eventChain()
          .write(Unpooled.wrappedBuffer(p.data()))
          .onComplete(e->System.out.printf("[TCP] Packet writed: remote=%s, bytes=%d, channel=%s%n", e.channel().remoteHost(), p.originalLength(), p.channelID()))
          .execute();
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
