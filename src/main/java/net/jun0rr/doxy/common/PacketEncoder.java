/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import us.pserver.tools.Unchecked;
import us.pserver.tools.io.BitBuffer;


/**
 *
 * @author juno
 */
public class PacketEncoder {
  
  private final Cipher cipher;
  
  public PacketEncoder(String algo, Key key) {
    cipher = Unchecked.call(()->Cipher.getInstance(algo));
    Unchecked.call(()->cipher.init(Cipher.ENCRYPT_MODE, key));
  }
  
  public ByteBuffer encrypt(ByteBuffer buf) {
    ByteBuffer encbuf = alloc(cipher.getOutputSize(buf.remaining()), buf.isDirect());
    Unchecked.call(()->cipher.doFinal(buf, encbuf));
    encbuf.flip();
    return encbuf;
  }
  
  public Packet encryptPacket(Packet p) {
    int orilen = p.data().remaining();
    return p.isEncoded() ? p 
        : Packet.of(p.channelID(), encrypt(p.data()), p.remote(), p.order(), orilen, true);
  }
  
  public Packet encodePacket(Packet p) {
    //return compressPacket(encryptPacket(p));
    return encryptPacket(p);
  }
  
  public ByteBuffer encode(Packet p) {
    return encodePacket(p).toByteBuffer();
  }
  
  public ByteBuffer compress(ByteBuffer buf) {
    BitBuffer out = BitBuffer.dynamicBuffer(buf.remaining(), buf.isDirect());
    try(GZIPOutputStream gout = new GZIPOutputStream(out.getOutputStream())) {
      byte[] bs = new byte[buf.remaining()];
      buf.get(bs);
      gout.write(bs);
      gout.finish();
      gout.flush();
      return out.flip().toByteBuffer();
    }
    catch(IOException e) {
      throw Unchecked.unchecked(e);
    }
  }
  
  public Packet compressPacket(Packet p) {
    return p.isEncoded() ? p 
        : Packet.of(p.channelID(), compress(p.data()), p.remote(), p.order(), p.originalLength(), true);
  }
  
  private ByteBuffer alloc(int size, boolean direct) {
    return direct ? ByteBuffer.allocateDirect(size) : ByteBuffer.allocate(size);
  }
  
}
