/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import net.jun0rr.doxy.tcp.handler.TcpUcaughtExceptionHandler;
import net.jun0rr.doxy.tcp.handler.TcpConnectHandler;
import net.jun0rr.doxy.tcp.handler.TcpInboundHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.jun0rr.doxy.common.AddingLastChannelInitializer;
import net.jun0rr.doxy.tcp.handler.TcpOutboundHandler;
import net.jun0rr.doxy.tcp.handler.TcpReadCompleteHandler;
import net.jun0rr.doxy.tcp.handler.TcpWriterHandler;
import us.pserver.tools.Indexed;


/**
 *
 * @author Juno
 */
public class TcpChannelHandlerSetup extends AbstractChannelHandlerSetup<TcpHandler> {
  
  public TcpChannelHandlerSetup() {
    super();
  }
  
  public static TcpChannelHandlerSetup newSetup() {
    return new TcpChannelHandlerSetup();
  }
  
  @Override
  public ChannelInitializer<SocketChannel> create(TcpChannel tch) {
    List<Supplier<ChannelHandler>> ls = new LinkedList<>();
    TcpWriterHandler writer = new TcpWriterHandler();
    ls.add(()->new TcpWriterHandler());
    Function<TcpHandler,Supplier<ChannelHandler>> ofn = h->()->new TcpOutboundHandler(tch, h);
    Function<Consumer<TcpExchange>,Supplier<ChannelHandler>> cfn = c->()->new TcpConnectHandler(tch, c);
    Function<TcpHandler,Supplier<ChannelHandler>> rfn = h->()->new TcpReadCompleteHandler(tch, h);
    Function<TcpHandler,Supplier<ChannelHandler>> ifn = h->()->new TcpInboundHandler(tch, h);
    outputHandlers().stream()
        .map(Indexed.builder())
        .sorted((a,b)->Integer.compare(b.index(), a.index()))
        .map(Indexed::value)
        .map(ofn)
        .forEach(ls::add);
    connectHandlers().stream().map(cfn).forEach(ls::add);
    readCompleteHandlers().stream().map(rfn).forEach(ls::add);
    inputHandlers().stream().map(ifn).forEach(ls::add);
    ls.add(()->new TcpUcaughtExceptionHandler());
    return new AddingLastChannelInitializer(sslHandlerFactory(), ls);
  }
  
}
