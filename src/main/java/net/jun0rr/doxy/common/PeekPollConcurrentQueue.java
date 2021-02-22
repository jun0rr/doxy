/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author juno
 */
public class PeekPollConcurrentQueue<T> implements Queue<T> {
  
  private final Queue<T> queue;
  
  public PeekPollConcurrentQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
  }

  @Override
  public boolean add(T e) {
    return queue.add(e);
  }

  @Override
  public boolean offer(T e) {
    return queue.offer(e);
  }

  @Override
  public T remove() {
    return queue.remove();
  }

  @Override
  public T poll() {
    return queue.peek();
  }

  @Override
  public T element() {
    return queue.element();
  }

  @Override
  public T peek() {
    return queue.peek();
  }

  @Override
  public int size() {
    return queue.size();
  }

  @Override
  public boolean isEmpty() {
    return queue.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return queue.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return queue.iterator();
  }

  @Override
  public Object[] toArray() {
    return queue.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return queue.toArray(a);
  }

  @Override
  public boolean remove(Object o) {
    return queue.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return queue.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return queue.addAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return queue.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return queue.retainAll(c);
  }

  @Override
  public void clear() {
    queue.clear();
  }
  
}
