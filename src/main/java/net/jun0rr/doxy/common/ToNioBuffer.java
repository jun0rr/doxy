/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.function.IntFunction;


/**
 *
 * @author Juno
 */
public interface ToNioBuffer {
  
  public static ByteBuffer apply(ByteBuf buf) {
    return apply(buf, ByteBuffer::allocate, true);
  }
  
  public static ByteBuffer apply(ByteBuf buf, IntFunction<ByteBuffer> alloc, boolean release) {
    ByteBuffer nio = alloc.apply(buf.readableBytes());
    buf.readBytes(nio);
    nio.flip();
    if(buf.refCnt() > 0) buf.release(buf.refCnt());
    return nio;
  }
  
}
