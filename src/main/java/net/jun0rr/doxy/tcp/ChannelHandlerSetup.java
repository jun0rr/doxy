/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


/**
 *
 * @author juno
 */
public interface ChannelHandlerSetup<H extends ChannelHandler> {
  
  public ChannelHandlerSetup<H> addConnectHandler(Consumer<TcpExchange> cs);
  
  public ChannelHandlerSetup<H> addReadCompleteHandler(H h);
  
  public ChannelHandlerSetup<H> addInputHandler(H h);
  
  public ChannelHandlerSetup<H> addOutputHandler(H h);
  
  public ChannelHandlerSetup<H> enableSSL(SSLHandlerFactory shf);
  
  public Optional<SSLHandlerFactory> disableSSL();
  
  public Optional<SSLHandlerFactory> sslHandlerFactory();
  
  public List<H> inputHandlers();
  
  public List<H> outputHandlers();
  
  public List<Consumer<TcpExchange>> connectHandlers();
  
  public List<H> readCompleteHandlers();
  
  public ChannelInitializer<SocketChannel> create(TcpChannel c);
  
}
