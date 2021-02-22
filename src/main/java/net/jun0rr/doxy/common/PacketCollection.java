/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;


/**
 *
 * @author Juno
 */
public class PacketCollection implements Collection<Packet> {
  
  private final List<Packet> packets;
  
  public PacketCollection(Collection<Packet> c) {
    this.packets = new ArrayList<>(c);
  }
  
  public PacketCollection() {
    this(Collections.EMPTY_LIST);
  }
  
  @Override
  public int size() {
    return packets.size();
  }
  
  @Override
  public boolean isEmpty() {
    return packets.isEmpty();
  }
  
  @Override
  public boolean contains(Object o) {
    return packets.contains(o);
  }
  
  @Override
  public Iterator<Packet> iterator() {
    return packets.iterator();
  }
  
  @Override
  public Object[] toArray() {
    return packets.toArray();
  }
  
  @Override
  public <T> T[] toArray(T[] a) {
    return packets.toArray(a);
  }
  
  @Override
  public boolean add(Packet e) {
    return packets.add(e);
  }
  
  @Override
  public boolean remove(Object o) {
    return packets.remove(o);
  }
  
  @Override
  public boolean containsAll(Collection<?> c) {
    return packets.containsAll(c);
  }
  
  @Override
  public boolean addAll(Collection<? extends Packet> c) {
    return packets.addAll(c);
  }
  
  @Override
  public boolean removeAll(Collection<?> c) {
    return packets.removeAll(c);
  }
  
  @Override
  public boolean retainAll(Collection<?> c) {
    return packets.retainAll(c);
  }
  
  @Override
  public void clear() {
    packets.clear();
  }
  
  public ByteBuffer toByteBuffer() {
    return toByteBuffer(ByteBuffer::allocateDirect);
  }
  
  public ByteBuffer toByteBuffer(IntFunction<ByteBuffer> alloc) {
    int len = packets.stream().mapToInt(Packet::encodeLength).sum() + Integer.BYTES * 2;
    ByteBuffer buf = alloc.apply(len);
    buf.putInt(len);
    buf.putInt(size());
    packets.stream().map(Packet::toByteBuffer).forEach(buf::put);
    buf.flip();
    return buf;
  }
  
  
  
  public static PacketCollection of(Collection<Packet> c) {
    return new PacketCollection(c);
  }
  
  
  
  public static PacketCollection of(ByteBuffer buf) {
    int len = buf.getInt();
    int size = buf.getInt();
    List<Packet> ls = new LinkedList<>();
    for(int i = 0; i < size; i++) {
      ls.add(Packet.of(buf));
    }
    return new PacketCollection(ls);
  }
  
}
