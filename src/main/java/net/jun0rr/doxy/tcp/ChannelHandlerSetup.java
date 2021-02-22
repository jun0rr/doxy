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
import java.util.function.Supplier;


/**
 *
 * @author juno
 */
public interface ChannelHandlerSetup<H extends ChannelHandler> {
  
  public ChannelHandlerSetup<H> addConnectHandler(Supplier<Consumer<TcpExchange>> sup);
  
  public ChannelHandlerSetup<H> addInputHandler(Supplier<H> sup);
  
  public ChannelHandlerSetup<H> addOutputHandler(Supplier<H> sup);
  
  public ChannelHandlerSetup<H> enableSSL(SSLHandlerFactory shf);
  
  public Optional<SSLHandlerFactory> disableSSL();
  
  public Optional<SSLHandlerFactory> sslHandlerFactory();
  
  public List<Supplier<H>> inputHandlers();
  
  public List<Supplier<H>> outputHandlers();
  
  public List<Supplier<Consumer<TcpExchange>>> connectHandlers();
  
  public ChannelInitializer<SocketChannel> create(TcpChannel c);
  
}
