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
import net.jun0rr.doxy.tcp.handler.TcpWriterHandler;


/**
 *
 * @author Juno
 */
public class TcpHandlerSetup extends AbstractChannelHandlerSetup<TcpHandler> {
  
  public TcpHandlerSetup() {
    super();
  }
  
  public static TcpHandlerSetup newSetup() {
    return new TcpHandlerSetup();
  }
  
  @Override
  public ChannelInitializer<SocketChannel> create(TcpChannel tch) {
    List<Supplier<ChannelHandler>> ls = new LinkedList<>();
    ls.add(TcpWriterHandler::new);
    Function<Supplier<TcpHandler>,Supplier<ChannelHandler>> ofn = s->()->new TcpOutboundHandler(tch, s.get());
    Function<Supplier<Consumer<TcpExchange>>,Supplier<ChannelHandler>> cfn = s->()->new TcpConnectHandler(tch, s.get());
    Function<Supplier<TcpHandler>,Supplier<ChannelHandler>> ifn = s->()->new TcpInboundHandler(tch, s.get());
    for(int i = outputHandlers().size() -1; i >= 0; i--) {
      ls.add(ofn.apply(outputHandlers().get(i)));
    }
    connectHandlers().stream().map(cfn).forEach(ls::add);
    inputHandlers().stream().map(ifn).forEach(ls::add);
    ls.add(TcpUcaughtExceptionHandler::new);
    return new AddingLastChannelInitializer(sslHandlerFactory(), ls);
  }
  
}
