/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
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
  
  public static ByteBuffer apply(HttpContent ct) {
    return apply(ct, ByteBuffer::allocate);
  }
  
  public static ByteBuffer apply(HttpContent ct, IntFunction<ByteBuffer> alloc) {
    ByteBuffer buffer = ByteBuffer.wrap(new byte[0]);
    if(ct.content() != null && ct.content().isReadable()) {
      buffer = apply(ct.content(), alloc, true);
    }
    if(ct.refCnt() > 0) ct.release(ct.refCnt());
    return buffer;
  }
  
  public static ByteBuffer apply(ByteBuf buf, IntFunction<ByteBuffer> alloc, boolean release) {
    ByteBuffer nio = alloc.apply(buf.readableBytes());
    buf.readBytes(nio);
    nio.flip();
    if(buf.refCnt() > 0) buf.release(buf.refCnt());
    return nio;
  }
  
}
