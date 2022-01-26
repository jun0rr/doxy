/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.ehcache.Cache;
import org.ehcache.Cache.Entry;
import us.pserver.tools.Hash;

/**
 *
 * @author juno
 */
public class TcpSession {
  
  private final String keyPrefix;
  
  private final Cache<String,Object> cache;
  
  private final TcpChannel channel;
  
  public TcpSession(TcpChannel channel, Cache<String,Object> cache, String prefix) {
    this.channel = Objects.requireNonNull(channel);
    this.cache = Objects.requireNonNull(cache);
    this.keyPrefix = prefix;
  }
  
  public TcpSession(TcpChannel channel, Cache<String,Object> cache) {
    this(channel, cache, null);
  }
  
  public static TcpSession of(TcpChannel channel, Cache<String,Object> cache, String prefix) {
    return new TcpSession(channel, cache, prefix);
  }
  
  public static TcpSession of(TcpChannel channel, Cache<String,Object> cache) {
    return new TcpSession(channel, cache);
  }
  
  public static TcpSession of(TcpChannel channel, TcpSession sess) {
    return new TcpSession(channel, sess.cache, sess.keyPrefix);
  }
  
  public static TcpSession prefixed(TcpChannel channel, Cache<String,Object> cache) {
    return new TcpSession(channel, cache, channelKey(channel));
  }
  
  public static TcpSession prefixed(TcpChannel channel, TcpSession sess) {
    String prefix = channelKey(channel);
    if(sess.keyPrefix != null) {
      prefix = String.format("%s.%s", sess.keyPrefix, prefix);
    }
    return new TcpSession(channel, sess.cache, prefix);
  }
  
  public TcpSession global() {
    if(keyPrefix != null && !keyPrefix.isBlank()) {
      return new TcpSession(channel, cache);
    }
    return this;
  }
  
  public TcpChannel channel() {
    return channel;
  }
  
  public static String channelKey(TcpChannel ch) {
    return Hash.md5().of(String.format("%s->%s", ch.localHost(), ch.remoteHost()));
  }
  
  private String prefixedKey(String k) {
    if(keyPrefix == null) return k;
    return String.format("%s.%s", keyPrefix, k);
  }
  
  public boolean contains(String key) {
    return cache.containsKey(prefixedKey(key));
  }
  
  public TcpSession put(String key, Object val) {
    if(key != null && val != null) {
      this.cache.put(prefixedKey(key), val);
    }
    return this;
  }
  
  public <T> Optional<T> get(String key) {
    if(key == null) return Optional.empty();
    Object o = this.cache.get(prefixedKey(key));
    if(o == null) return Optional.empty();
    return Optional.of((T)o);
  }
  
  public void remove(String key) {
    if(key != null) this.cache.remove(prefixedKey(key));
  }
  
  public void clear() {
    if(keyPrefix != null) {
      keys().filter(k->k.startsWith(keyPrefix)).forEach(cache::remove);
    }
    else {
      cache.clear();
    }
  }
  
  public Stream<Entry<String,Object>> entries() {
    return StreamSupport.stream(cache.spliterator(), false);
  }
  
  public Stream<String> keys() {
    return entries().map(Entry::getKey);
  }
  
  public Stream<Object> values() {
    return entries().map(Entry::getValue);
  }
  
  private <T> Entry<String,T> entry(Entry<String,Object> e, Class<T> cls) {
    return new Entry<String,T>() {
      public String getKey() { return e.getKey(); }
      public T getValue() { return cls.cast(e.getValue()); }
    };
  }
  
  public <T> Stream<Entry<String,T>> entriesOf(Class<T> cls) {
    return StreamSupport.stream(cache.spliterator(), false)
        .filter(e->cls.isAssignableFrom(e.getValue().getClass()))
        .map(e->entry(e, cls));
  }
  
  public <T> Stream<T> valuesOf(Class<T> cls) {
    return entriesOf(cls).map(Entry::getValue);
  }
  
}
