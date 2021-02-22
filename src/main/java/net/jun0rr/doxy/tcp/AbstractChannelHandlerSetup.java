/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 *
 * @author Juno
 */
public abstract class AbstractChannelHandlerSetup<H extends ChannelHandler> implements ChannelHandlerSetup<H> {

  private final List<Supplier<H>> inputHandlers;
  
  private final List<Supplier<H>> outputHandlers;
  
  private final List<Supplier<Consumer<TcpExchange>>> connectHandlers;
  
  private Optional<SSLHandlerFactory> sslHandlerFactory;
  
  protected AbstractChannelHandlerSetup() {
    this.inputHandlers = new LinkedList<>();
    this.outputHandlers = new LinkedList<>();
    this.connectHandlers = new LinkedList<>();
    this.sslHandlerFactory = Optional.empty();
  }
  
  @Override
  public ChannelHandlerSetup<H> addConnectHandler(Supplier<Consumer<TcpExchange>> sup) {
    if(sup != null) {
      this.connectHandlers.add(sup);
    }
    return this;
  }
  
  @Override
  public ChannelHandlerSetup<H> addInputHandler(Supplier<H> sup) {
    if(sup != null) {
      this.inputHandlers.add(sup);
    }
    return this;
  }
  
  @Override
  public ChannelHandlerSetup<H> addOutputHandler(Supplier<H> sup) {
    if(sup != null) {
      this.outputHandlers.add(sup);
    }
    return this;
  }
  
  @Override
  public ChannelHandlerSetup<H> enableSSL(SSLHandlerFactory shf) {
    this.sslHandlerFactory = Optional.ofNullable(shf);
    return this;
  }
  
  @Override
  public Optional<SSLHandlerFactory> disableSSL() {
    final Optional<SSLHandlerFactory> ret = this.sslHandlerFactory;
    this.sslHandlerFactory = Optional.empty();
    return ret;
  }
  
  @Override
  public Optional<SSLHandlerFactory> sslHandlerFactory() {
    return this.sslHandlerFactory;
  }
  
  @Override
  public List<Supplier<H>> inputHandlers() {
    return inputHandlers;
  }
  
  @Override
  public List<Supplier<H>> outputHandlers() {
    return outputHandlers;
  }
  
  @Override
  public List<Supplier<Consumer<TcpExchange>>> connectHandlers() {
    return connectHandlers;
  }
  
}
