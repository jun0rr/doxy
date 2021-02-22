/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.util.Objects;
import java.util.function.Consumer;


/**
 *
 * @author juno
 */
public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
  
  private final Consumer<SocketChannel> cs;
  
  public ConsumerChannelInitializer(Consumer<SocketChannel> cs) {
    this.cs = Objects.requireNonNull(cs, "Bad null Consumer");
  }

  @Override
  protected void initChannel(SocketChannel c) throws Exception {
    cs.accept(c);
  }
  
}
