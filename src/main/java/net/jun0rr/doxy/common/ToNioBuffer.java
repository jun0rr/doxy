/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;


/**
 *
 * @author Juno
 */
public interface ToNioBuffer {
  
  public static ByteBuffer apply(ByteBuf buf) {
    ByteBuffer nio = ByteBuffer.allocate(buf.readableBytes());
    buf.readBytes(nio);
    nio.flip();
    buf.release();
    return nio;
  }
  
}
