/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;


/**
 *
 * @author juno
 */
public class AddingLastChannelInitializer extends ChannelInitializer<SocketChannel> {
  
  private final Collection<Supplier<ChannelHandler>> handlers;
  
  private final Optional<SSLHandlerFactory> sslFactory;
  
  public AddingLastChannelInitializer(Supplier<ChannelHandler>... handlers) {
    this(Arrays.asList(handlers));
  }
  
  public AddingLastChannelInitializer(Optional<SSLHandlerFactory> sslFactory, Supplier<ChannelHandler>... handlers) {
    this(sslFactory, Arrays.asList(handlers));
  }
  
  public AddingLastChannelInitializer(Collection<Supplier<ChannelHandler>> handlers) {
    this(null, handlers);
  }
  
  public AddingLastChannelInitializer(Optional<SSLHandlerFactory> sslFactory, Collection<Supplier<ChannelHandler>> handlers) {
    this.handlers = Collections.unmodifiableCollection(
        Objects.requireNonNull(handlers, "Bad null ChannelHandler Collection")
    );
    if(handlers.isEmpty()) {
      throw new IllegalArgumentException("ChannelHandler Collection is Empty!");
    }
    this.sslFactory = Objects.requireNonNull(sslFactory, "Bad null SSLHandlerFactory Optional");
  }
  
  @Override
  protected void initChannel(SocketChannel c) throws Exception {
    sslFactory.ifPresent(f->c.pipeline().addLast(f.create(c.alloc())));
    handlers.stream()
        .map(Supplier::get)
        .forEach(c.pipeline()::addLast);
  }
  
}
