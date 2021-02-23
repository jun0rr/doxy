/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.jun0rr.doxy.common.InstanceOf;
import net.jun0rr.doxy.tcp.ConnectedTcpChannel;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpExchange;


/**
 *
 * @author Juno
 */
public interface HttpExchange extends TcpExchange {
  
  public static final ThreadLocal<HttpExchange> CURRENT_EXCHANGE = new ThreadLocal<>();
  
  
  
  public HttpRequest request();
  
  public HttpResponse response();
  
  public HttpExchange withRequest(HttpRequest req);
  
  public HttpExchange withResponse(HttpResponse res);
  
  /**
   * Return a new HttpExchange with the informed message object.
   * @param msg The message object can be either a HttpRequest, HttpResponse or the HttpResponse body.
   * @return Optional filled with a new HttpExchange.
   */
  @Override 
  public HttpExchange withMessage(Object msg);
  
  @Override
  public HttpExchange setAttr(String key, Object val);
  
  /**
   * Return this HttpExchange (filled Optional).
   * @return Optional filled with this HttpExchange.
   */
  @Override 
  public Optional<HttpExchange> forward();
  
  /**
   * Return an empty Optional.
   * @return Empty Optional.
   */
  @Override 
  public Optional<HttpExchange> empty();
  
  @Override 
  public Optional<HttpExchange> send();
  
  @Override
  public Optional<HttpExchange> sendAndClose();
  
  @Override 
  public HttpRequest message();
  
  
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected, HttpRequest req, HttpResponse res) {
    return new HttpExchangeImpl(channel, connected, req, res);
  }
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected, HttpRequest req) {
    return new HttpExchangeImpl(channel, connected, req);
  }
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected) {
    return  new HttpExchangeImpl(channel, connected);
  }
  
  
  
  
  
  public static class HttpExchangeImpl extends TcpExchange.TcpExchangeImpl implements HttpExchange {
    
    private final HttpRequest request;
    
    private final HttpResponse response;
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, HttpRequest req, HttpResponse res) {
      super(boot, connected, new ConcurrentHashMap<>(), req);
      this.request = Objects.requireNonNull(req, "Bad null HttpRequest");
      this.response = Objects.requireNonNull(res, "Bad null HttpResponse");
      CURRENT_EXCHANGE.set(this);
    }
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, HttpRequest req) {
      super(boot, connected, new ConcurrentHashMap<>(), req);
      this.request = Objects.requireNonNull(req, "Bad null HttpRequest");
      this.response = HttpResponse.of(HttpResponseStatus.OK);
      CURRENT_EXCHANGE.set(this);
    }
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected) {
      super(boot, connected, new ConcurrentHashMap<>(), HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"));
      this.request = this.message();
      this.response = HttpResponse.of(HttpResponseStatus.OK);
      CURRENT_EXCHANGE.set(this);
    }
    
    @Override
    public HttpRequest request() {
      return request;
    }
    
    @Override
    public HttpResponse response() {
      return response;
    }
    
    @Override
    public HttpExchange withRequest(HttpRequest req) {
      request.dispose();
      return new HttpExchangeImpl(boot, connected, req, response);
    }
    
    @Override
    public HttpExchange withResponse(HttpResponse res) {
      response.dispose();
      return new HttpExchangeImpl(boot, connected, request, res);
    }
    
    @Override
    public HttpExchange setAttr(String key, Object val) {
      super.setAttr(key, val);
      return this;
    }
    
    private HttpResponse responseWith(Object msg) {
      return response.withMessage((msg instanceof CharSequence) 
          ? Unpooled.copiedBuffer((CharSequence)msg, StandardCharsets.UTF_8) 
          : msg
      );
    }
    
    @Override
    public Optional<HttpExchange> empty() {
      return Optional.empty();
    }
    
    @Override
    public Optional<HttpExchange> send() {
      connected.events().write(this).execute();
      return empty();
    }
    
    @Override
    public Optional<HttpExchange> sendAndClose() {
      response().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
      connected.events().write(this).close().execute();
      return empty();
    }
    
    @Override
    public HttpRequest message() {
      return request;
    }
    
    @Override
    public HttpExchange withMessage(Object msg) {
      return InstanceOf.of(HttpRequest.class, this::withRequest)
          .elseOf(FullHttpRequest.class, o->withRequest(HttpRequest.of(o)))
          .elseOf(HttpResponse.class, this::withResponse)
          .elseOf(FullHttpResponse.class, o->withResponse(HttpResponse.of(o)))
          .elseThen(o->new HttpExchangeImpl(boot, connected, request, responseWith(o)))
          .apply(msg).get();
    }
    
    @Override
    public Optional<HttpExchange> forward() {
      return Optional.of(this);
    }
    
  }
  
}
