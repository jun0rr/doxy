/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import net.jun0rr.doxy.http.handler.HttpConnectHandler;
import net.jun0rr.doxy.http.handler.HttpInboundHandler;
import net.jun0rr.doxy.tcp.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.jun0rr.doxy.common.AddingLastChannelInitializer;
import net.jun0rr.doxy.http.handler.HttpClientWriterHandler;


/**
 *
 * @author Juno
 */
public class HttpClientHandlerSetup extends AbstractChannelHandlerSetup<HttpHandler> {
  
  
  public HttpClientHandlerSetup() {
    super();
  }
  
  public static HttpClientHandlerSetup newSetup() {
    return new HttpClientHandlerSetup();
  }
  
  @Override
  public ChannelInitializer<SocketChannel> create(TcpChannel tch) {
    List<Supplier<ChannelHandler>> ls = new LinkedList<>();
    Function<Supplier<Consumer<TcpExchange>>,Supplier<ChannelHandler>> cfn = s->()->new HttpConnectHandler(tch, s.get());
    Function<Supplier<HttpHandler>,Supplier<ChannelHandler>> ifn = s->()->new HttpInboundHandler(tch, s.get());
    ls.add(HttpClientCodec::new);
    ls.add(()->new HttpClientWriterHandler());
    ls.add(()->new HttpObjectAggregator(1024*1024));
    connectHandlers().stream().map(cfn).forEach(ls::add);
    inputHandlers().stream().map(ifn).forEach(ls::add);
    return new AddingLastChannelInitializer(sslHandlerFactory(), ls);
  }
  
}
