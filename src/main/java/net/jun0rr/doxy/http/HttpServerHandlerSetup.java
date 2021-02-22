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
import io.netty.handler.codec.http.HttpObjectAggregator;
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
import net.jun0rr.doxy.common.AddingLastChannelInitializer;
import net.jun0rr.doxy.http.handler.HttpOutboundHandler;
import net.jun0rr.doxy.http.handler.HttpServerWriterHandler;
import net.jun0rr.doxy.http.handler.HttpRouteHandler;
import net.jun0rr.doxy.http.handler.HttpServerErrorHandler;
import net.jun0rr.doxy.http.handler.HttpUncaughtExceptionHandler;
import net.jun0rr.doxy.http.handler.RoutableHttpHandler;


/**
 *
 * @author Juno
 */
public class HttpServerHandlerSetup extends AbstractChannelHandlerSetup<HttpHandler> {
  
  public static final String SERVER_NAME = "doxy-http-server";
  
  private final Map<HttpRoute, Supplier<HttpHandler>> routeHandlers;
  
  private BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> uncaughtExceptionHandler;
  
  private Supplier<HttpHandler> defaultHandler;
  
  private final HttpHeaders headers;
  
  private String serverName;
  
  
  public HttpServerHandlerSetup(String serverName) {
    super();
    this.routeHandlers = new HashMap<>();
    this.defaultHandler = ()->HttpHandler.BAD_REQUEST;
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
  
  public HttpServerHandlerSetup addOutputHandler(Supplier<HttpHandler> sup) {
    super.addOutputHandler(sup);
    return this;
  }
  
  public HttpServerHandlerSetup addRouteHandler(HttpRoute r, Supplier<HttpHandler> s) {
    if(r != null && s != null) {
      routeHandlers.put(r, s);
    }
    return this;
  }
  
  public Map<HttpRoute,Supplier<HttpHandler>> routeHandlers() {
    return routeHandlers;
  }
  
  public HttpServerHandlerSetup setDefaultHandler(Supplier<HttpHandler> s) {
    if(s != null) {
      defaultHandler = s;
    }
    return this;
  }
  
  public Supplier<HttpHandler> getDefaultHandler() {
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
  
  @Override
  public ChannelInitializer<SocketChannel> create(TcpChannel tch) {
    List<Supplier<ChannelHandler>> ls = new LinkedList<>();
    List<RoutableHttpHandler> routables = new LinkedList<>();
    Function<Supplier<Consumer<TcpExchange>>,Supplier<ChannelHandler>> cfn = s->()->new HttpConnectHandler(tch, s.get());
    Function<Supplier<HttpHandler>,Supplier<ChannelHandler>> ifn = s->()->new HttpInboundHandler(tch, s.get());
    Function<Supplier<HttpHandler>,Supplier<ChannelHandler>> ofn = s->()->new HttpOutboundHandler(tch, s.get(), uncaughtExceptionHandler);
    Function<Map.Entry<HttpRoute,Supplier<HttpHandler>>, Supplier<RoutableHttpHandler>> rfn = e->
        ()->RoutableHttpHandler.of(e.getKey(), e.getValue().get());
    routeHandlers().entrySet().stream().forEach(e->
      routables.add(RoutableHttpHandler.of(e.getKey(), e.getValue().get()))
    );
    ls.add(HttpServerCodec::new);
    ls.add(()->new HttpServerWriterHandler(serverName, headers, uncaughtExceptionHandler));
    outputHandlers().stream().map(ofn).forEach(ls::add);
    ls.add(()->new HttpObjectAggregator(1024*1024));
    connectHandlers().stream().map(cfn).forEach(ls::add);
    inputHandlers().stream().map(ifn).forEach(ls::add);
    if(!routables.isEmpty()) ls.add(ifn.apply(()->new HttpRouteHandler(defaultHandler.get(), routables)));
    ls.add(()->new HttpUncaughtExceptionHandler(uncaughtExceptionHandler));
    return new AddingLastChannelInitializer(sslHandlerFactory(), ls);
  }
  
}
