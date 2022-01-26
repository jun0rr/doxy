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
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpServerCodec;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.jun0rr.doxy.common.AddingLastChannelInitializer;
import net.jun0rr.doxy.http.handler.HttpOutboundHandler;
import net.jun0rr.doxy.http.handler.HttpReadCompleteHandler;
import net.jun0rr.doxy.http.handler.HttpServerWriterHandler;
import net.jun0rr.doxy.http.handler.HttpRouteHandler;
import net.jun0rr.doxy.http.handler.HttpServerErrorHandler;
import net.jun0rr.doxy.http.handler.HttpUncaughtExceptionHandler;
import net.jun0rr.doxy.http.handler.RoutableHttpHandler;
import us.pserver.tools.Indexed;
import us.pserver.tools.Pair;


/**
 *
 * @author Juno
 */
public class HttpServerHandlerSetup extends AbstractChannelHandlerSetup<HttpHandler> {
  
  public static final String SERVER_NAME = "doxy-http-server";
  
  private final Map<HttpRoute, HttpHandler> routeHandlers;
  
  private BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtExceptionHandler;
  
  private HttpHandler defaultHandler;
  
  private final HttpHeaders headers;
  
  private String serverName;
  
  
  public HttpServerHandlerSetup(String serverName) {
    super();
    this.routeHandlers = new HashMap<>();
    this.defaultHandler = HttpHandler.BAD_REQUEST;
    this.uncaughtExceptionHandler = new HttpServerErrorHandler();
    this.serverName = Objects.requireNonNull(serverName, "Bad null server name");
    this.headers = new DefaultHttpHeaders();
  }
  
  public HttpServerHandlerSetup() {
    this(SERVER_NAME);
  }
  
  public static HttpServerHandlerSetup newSetup() {
    return new HttpServerHandlerSetup();
  }
  
  public HttpHeaders responseHeaders() {
    return headers;
  }
  
  @Override
  public HttpServerHandlerSetup enableSSL(SSLHandlerFactory ssl) {
    super.enableSSL(ssl);
    return this;
  }
  
  @Override
  public HttpServerHandlerSetup addConnectHandler(Consumer<TcpExchange> cs) {
    super.addConnectHandler(cs);
    return this;
  }
  
  @Override
  public HttpServerHandlerSetup addReadCompleteHandler(HttpHandler h) {
    super.addReadCompleteHandler(h);
    return this;
  }
  
  @Override
  public HttpServerHandlerSetup addInputHandler(HttpHandler h) {
    super.addInputHandler(h);
    return this;
  }
  
  @Override
  public HttpServerHandlerSetup addOutputHandler(HttpHandler h) {
    super.addOutputHandler(h);
    return this;
  }
  
  public HttpServerHandlerSetup addRouteHandler(HttpRoute r, HttpHandler s) {
    if(r != null && s != null) {
      routeHandlers.put(r, s);
    }
    return this;
  }
  
  public Map<HttpRoute,HttpHandler> routeHandlers() {
    return routeHandlers;
  }
  
  public HttpServerHandlerSetup setDefaultHandler(HttpHandler s) {
    if(s != null) {
      defaultHandler = s;
    }
    return this;
  }
  
  public HttpHandler getDefaultHandler() {
    return defaultHandler;
  }
  
  public HttpServerHandlerSetup setUncaughtExceptionHandler(BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> fn) {
    if(fn != null) {
      this.uncaughtExceptionHandler = fn;
    }
    return this;
  }
  
  public BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> getUncaughtExceptionHandler() {
    return uncaughtExceptionHandler;
  }
  
  public String getServerName() {
    return serverName;
  }
  
  public HttpServerHandlerSetup setServerName(String name) {
    if(name != null && !name.isBlank()) {
      this.serverName = name;
    }
    return this;
  }
  
  private String oid(Object o) {
    return String.format("%s@%d", o.getClass().getName(), o.hashCode());
  }
  
  @Override
  public ChannelInitializer<SocketChannel> create(TcpChannel tch) {
    List<Supplier<ChannelHandler>> ls = new LinkedList<>();
    ls.add(()->new HttpServerCodec());
    ls.add(()->new HttpServerWriterHandler(serverName, headers, uncaughtExceptionHandler));
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
    List<RoutableHttpHandler> routables = routeHandlers.entrySet().stream()
        .map(e->new RoutableHttpHandler(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
    if(!routables.isEmpty()) {
      ls.add(()->new HttpInboundHandler(tch, new HttpRouteHandler(defaultHandler, routables)));
    }
    ls.add(()->new HttpUncaughtExceptionHandler(uncaughtExceptionHandler));
    return new AddingLastChannelInitializer(sslHandlerFactory(), ls);
  }
  
}
