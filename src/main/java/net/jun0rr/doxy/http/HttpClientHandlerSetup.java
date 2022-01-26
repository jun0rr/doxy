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
import io.netty.handler.codec.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.jun0rr.doxy.common.AddingLastChannelInitializer;
import net.jun0rr.doxy.http.handler.HttpClientWriterHandler;
import net.jun0rr.doxy.http.handler.HttpOutboundHandler;
import net.jun0rr.doxy.http.handler.HttpReadCompleteHandler;
import net.jun0rr.doxy.http.handler.HttpServerErrorHandler;
import us.pserver.tools.Indexed;


/**
 *
 * @author Juno
 */
public class HttpClientHandlerSetup extends AbstractChannelHandlerSetup<HttpHandler> {
  
  private BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtExceptionHandler;
  
  public HttpClientHandlerSetup() {
    super();
    this.uncaughtExceptionHandler = new HttpServerErrorHandler();
  }
  
  public static HttpClientHandlerSetup newSetup() {
    return new HttpClientHandlerSetup();
  }
  
  @Override
  public ChannelInitializer<SocketChannel> create(TcpChannel tch) {
    List<Supplier<ChannelHandler>> ls = new LinkedList<>();
    HttpClientCodec codec = new HttpClientCodec();
    ls.add(()->new HttpClientCodec());
    ls.add(()->new HttpClientWriterHandler());
    Function<HttpHandler,Supplier<ChannelHandler>> ofn = h->()->new HttpOutboundHandler(tch, h, uncaughtExceptionHandler);
    Function<Consumer<TcpExchange>,Supplier<ChannelHandler>> cfn = c->()->new HttpConnectHandler(tch, c);
    Function<HttpHandler,Supplier<ChannelHandler>> rfn = h->()->new HttpReadCompleteHandler(tch, h);
    Function<HttpHandler,Supplier<ChannelHandler>> ifn = h->()->new HttpInboundHandler(tch, h);
    outputHandlers().stream()
        .map(Indexed.builder())
        .sorted((a,b)->Integer.compare(b.index(), a.index()))
        .map(Indexed::value)
        .map(ofn)
        .forEach(ls::add);
    connectHandlers().stream().map(cfn).forEach(ls::add);
    readCompleteHandlers().stream().map(rfn).forEach(ls::add);
    inputHandlers().stream().map(ifn).forEach(ls::add);
    return new AddingLastChannelInitializer(sslHandlerFactory(), ls);
  }
  
}
