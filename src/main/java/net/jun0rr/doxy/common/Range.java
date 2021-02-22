/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.util.stream.IntStream;


/**
 *
 * @author Juno
 */
public class Range implements Comparable<Range> {
  
  public static final Range EMPTY = new Range(0,0);
  
  public static final Range ALL = new Range(Integer.MIN_VALUE, Integer.MAX_VALUE);
  
  
  private final int min;
  
  private final int max;
  
  public Range(int min, int max) {
    this.min = min;
    this.max = max;
  }
  
  public static Range closed(int n) {
    return new Range(n, n);
  }
  
  public static Range of(int min, int max) {
    return new Range(min, max);
  }
  
  public static Range of(String str) {
    String[] ss = str.substring(1, str.length()-1).split("-");
    return Range.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
  }
  
  public Range withMin(int min) {
    return new Range(min, max);
  }
  
  public Range withMax(int max) {
    return new Range(min, max);
  }
  
  public int min() {
    return min;
  }
  
  public int max() {
    return max;
  }
  
  public boolean contains(Range r) {
    return min <= r.min() && max >= r.max();
  }
  
  public boolean intersect(Range r) {
    return min <= r.min() || max >= r.max();
  }
  
  public int length() {
    return max - min;
  }
  
  public boolean isEmpty() {
    return length() == 0;
  }
  
  public IntStream stream() {
    return IntStream.rangeClosed(min, max);
  }
  
  @Override
  public int compareTo(Range r) {
    return (min() == r.min())
        ? Integer.compare(length(), r.length()) 
        : Integer.compare(min(), r.min());
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + this.min;
    hash = 31 * hash + this.max;
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
    final Range other = (Range) obj;
    if (this.min != other.min) {
      return false;
    }
    if (this.max != other.max) {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString() {
    return String.format("%d-%d", min, max);
  }
  
}
