/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.jun0rr.doxy.http.HttpMessages.HttpRequestBuilder;
import net.jun0rr.doxy.http.HttpMessages.HttpResponseBuilder;
import net.jun0rr.doxy.tcp.ConnectedTcpChannel;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpExchange;


/**
 *
 * @author Juno
 */
public interface HttpExchange extends TcpExchange {
  
  public static final HttpResponse RESPONSE_OK = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
  
  public static final HttpRequest REQUEST_GET_ROOT = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
  
  
  public static final ThreadLocal<HttpExchange> CURRENT_EXCHANGE = new ThreadLocal<>();
  
  
  public HttpRequest request();
  
  public HttpResponse response();
  
  public HttpExchange request(HttpRequest req);
  
  public HttpExchange response(HttpResponse res);
  
  public HttpResponseBuilder responseBuilder();
  
  public HttpRequestBuilder requestBuilder();
  
  public HttpExchange requestBody(ByteBuf msg);
  
  public HttpExchange responseBody(ByteBuf msg);
  
  public boolean isHttpMessage();

  public boolean isHttpContent();

  public boolean isLastHttpContent();

  public boolean isEmptyLastContent();

  /**
   * Return a new HttpExchange with the informed message object.
   * @param msg The message object can be either a HttpRequest, HttpResponse or the HttpResponse body.
   * @return Optional filled with a new HttpExchange.
   */
  @Override 
  public HttpExchange message(Object msg);
  
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
  public Optional<HttpExchange> sendAndFlush();
  
  @Override
  public Optional<HttpExchange> sendAndClose();
  
  
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected, HttpRequest req, HttpResponse res, Object message) {
    return new HttpExchangeImpl(channel, connected, req, res, message);
  }
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected, HttpRequest req) {
    return new HttpExchangeImpl(channel, connected, req);
  }
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected, HttpResponse res) {
    return new HttpExchangeImpl(channel, connected, res);
  }
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected, Object msg) {
    return new HttpExchangeImpl(channel, connected, msg);
  }
  
  public static HttpExchange of(TcpChannel channel, ConnectedTcpChannel connected) {
    return  new HttpExchangeImpl(channel, connected);
  }
  
  
  
  
  
  public static class HttpExchangeImpl extends TcpExchange.TcpExchangeImpl implements HttpExchange {
    
    private final HttpRequest request;
    
    private final HttpResponse response;
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, HttpRequest req, HttpResponse res, Object msg) {
      super(boot, connected, msg);
      this.request = Objects.requireNonNull(req, "Bad null HttpRequest");
      this.response = Objects.requireNonNull(res, "Bad null HttpResponse");
      CURRENT_EXCHANGE.set(this);
    }
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, Object msg) {
      super(boot, connected, msg);
      this.request = CURRENT_EXCHANGE.get() != null && CURRENT_EXCHANGE.get().request() != null
          ? CURRENT_EXCHANGE.get().request()
          : REQUEST_GET_ROOT;
      this.response = CURRENT_EXCHANGE.get() != null && CURRENT_EXCHANGE.get().response() != null
          ? CURRENT_EXCHANGE.get().response()
          : RESPONSE_OK;
      CURRENT_EXCHANGE.set(this);
    }
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, HttpRequest req) {
      super(boot, connected, req);
      this.request = Objects.requireNonNull(req, "Bad null HttpRequest");
      this.response = CURRENT_EXCHANGE.get() != null && CURRENT_EXCHANGE.get().response() != null
          ? CURRENT_EXCHANGE.get().response()
          : RESPONSE_OK;
      CURRENT_EXCHANGE.set(this);
    }
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected, HttpResponse res) {
      super(boot, connected, res);
      this.request = CURRENT_EXCHANGE.get() != null && CURRENT_EXCHANGE.get().request() != null
          ? CURRENT_EXCHANGE.get().request()
          : REQUEST_GET_ROOT;
      this.response = Objects.requireNonNull(res, "Bad null HttpResponse");
      CURRENT_EXCHANGE.set(this);
    }
    
    public HttpExchangeImpl(TcpChannel boot, ConnectedTcpChannel connected) {
      super(boot, connected, REQUEST_GET_ROOT);
      this.request = this.message();
      this.response = RESPONSE_OK;
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
    public HttpExchange request(HttpRequest req) {
      return new HttpExchangeImpl(boot, connected, req, response, req);
    }
    
    @Override
    public HttpExchange response(HttpResponse res) {
      return new HttpExchangeImpl(boot, connected, request, res, res);
    }
    
    @Override
    public HttpRequestBuilder requestBuilder() {
      return HttpMessages.request((Function<HttpRequest,HttpExchange>)this::request)
          .version(request.protocolVersion())
          .method(request.method())
          .headers(request.headers())
          .body(message != null && message instanceof ByteBuf
              ? this.<ByteBuf>message() : null)
          .body(message != null && message instanceof HttpContent
              ? this.<HttpContent>message().content() : null);
    }
    
    @Override
    public HttpResponseBuilder responseBuilder() {
      return HttpMessages.response((Function<HttpResponse,HttpExchange>)this::response)
          .version(response.protocolVersion())
          .status(response.status())
          .body(message != null && message instanceof ByteBuf
              ? this.<ByteBuf>message() : null)
          .body(message != null && message instanceof HttpContent
              ? this.<HttpContent>message().content() : null);
    }
    
    @Override
    public HttpExchange requestBody(ByteBuf msg) {
      return requestBuilder().body(msg).done();
    }
    
    @Override
    public HttpExchange responseBody(ByteBuf msg) {
      return responseBuilder().body(msg).done();
    }
    
    public HttpExchange lastContent() {
      return message(message != null && message instanceof ByteBuf 
          ? new DefaultLastHttpContent(this.<ByteBuf>message()) 
          : LastHttpContent.EMPTY_LAST_CONTENT);
    }
    
    @Override
    public boolean isHttpMessage() {
      return message != null 
          && message instanceof HttpMessage;
    }
    
    @Override
    public boolean isHttpContent() {
      return message != null 
          && !isEmptyLastContent()
          && (message instanceof HttpContent
          || !isHttpMessage());
    }
    
    @Override
    public boolean isLastHttpContent() {
      return message != null 
          && message instanceof LastHttpContent;
    }
    
    @Override
    public boolean isEmptyLastContent() {
      return message != null 
          && message instanceof LastHttpContent
          && message == LastHttpContent.EMPTY_LAST_CONTENT;
    }
    
    @Override
    public Optional<HttpExchange> empty() {
      return Optional.empty();
    }
    
    @Override
    public Optional<HttpExchange> send() {
      connected.events().write(this);
      return empty();
    }
    
    @Override
    public Optional<HttpExchange> sendAndFlush() {
      connected.events().writeAndFlush(this);
      return empty();
    }
    
    @Override
    public Optional<HttpExchange> sendAndClose() {
      response().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
      connected.events().writeAndFlush(this).close();
      return empty();
    }
    
    @Override
    public HttpExchange message(Object msg) {
      if(msg instanceof HttpRequest) {
        return request((HttpRequest)msg);
      }
      else if(msg instanceof HttpResponse) {
        return response((HttpResponse) msg);
      }
      else {
        return new HttpExchangeImpl(boot, connected, request, response, msg);
      }
    }
    
    @Override
    public Optional<HttpExchange> forward() {
      return Optional.of(this);
    }
    
    @Override
    public String toString() {
      return String.format("HttpExchange{request=[%s %s %s], response=[%s %s], message=%s}", 
          request.method(), request.uri(), request.protocolVersion(), 
          response.status(), response.protocolVersion(), 
          message() != null ? message().getClass().getSimpleName() : message()
      );
    }
    
  }
  
}
