/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.client;

import cn.danielw.fop.ObjectFactory;
import java.util.Objects;
import java.util.function.Supplier;
import net.jun0rr.doxy.tcp.TcpChannel;

/**
 *
 * @author juno
 */
public class TcpChannelFactory implements ObjectFactory<TcpChannel> {
  
  private final Supplier<TcpChannel> factory;
  
  public TcpChannelFactory(Supplier<TcpChannel> factory) {
    this.factory = Objects.requireNonNull(factory, "Bad null TcpChannel Supplier");
  }

  @Override
  public TcpChannel create() {
    return factory.get();
  }

  @Override
  public void destroy(TcpChannel t) {
    t.events().close();
  }

  @Override
  public boolean validate(TcpChannel t) {
    return t.nettyChannel().isOpen();
  }
  
}
