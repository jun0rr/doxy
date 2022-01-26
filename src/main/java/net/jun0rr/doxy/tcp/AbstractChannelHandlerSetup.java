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


/**
 *
 * @author Juno
 */
public abstract class AbstractChannelHandlerSetup<H extends ChannelHandler> implements ChannelHandlerSetup<H> {

  protected final List<H> inputHandlers;
  
  protected final List<H> outputHandlers;
  
  protected final List<Consumer<TcpExchange>> connectHandlers;
  
  protected final List<H> readCompleteHandlers;
  
  protected Optional<SSLHandlerFactory> sslHandlerFactory;
  
  protected AbstractChannelHandlerSetup() {
    this.inputHandlers = new LinkedList<>();
    this.outputHandlers = new LinkedList<>();
    this.connectHandlers = new LinkedList<>();
    this.readCompleteHandlers = new LinkedList<>();
    this.sslHandlerFactory = Optional.empty();
  }
  
  @Override
  public ChannelHandlerSetup<H> addConnectHandler(Consumer<TcpExchange> cs) {
    if(cs != null) {
      this.connectHandlers.add(cs);
    }
    return this;
  }
  
  @Override
  public ChannelHandlerSetup<H> addReadCompleteHandler(H h) {
    if(h != null) {
      this.readCompleteHandlers.add(h);
    }
    return this;
  }
  
  @Override
  public ChannelHandlerSetup<H> addInputHandler(H h) {
    if(h != null) {
      this.inputHandlers.add(h);
    }
    return this;
  }
  
  @Override
  public ChannelHandlerSetup<H> addOutputHandler(H h) {
    if(h != null) {
      this.outputHandlers.add(h);
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
  public List<H> inputHandlers() {
    return inputHandlers;
  }
  
  @Override
  public List<H> outputHandlers() {
    return outputHandlers;
  }
  
  @Override
  public List<Consumer<TcpExchange>> connectHandlers() {
    return connectHandlers;
  }
  
  @Override
  public List<H> readCompleteHandlers() {
    return readCompleteHandlers;
  }
  
}
