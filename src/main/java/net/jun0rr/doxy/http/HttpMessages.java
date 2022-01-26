/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.AsciiString;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author juno
 */
public interface HttpMessages {
  
  public static HttpResponseBuilder response() {
    return new HttpResponseBuilder();
  }
  
  public static HttpResponseBuilder response(Function<? extends HttpMessage,HttpExchange> apply) {
    return new HttpResponseBuilder(apply);
  }
  
  public static HttpRequestBuilder request() {
    return new HttpRequestBuilder();
  }
  
  public static HttpRequestBuilder request(Function<? extends HttpMessage,HttpExchange> apply) {
    return new HttpRequestBuilder(apply);
  }
  
  public static HttpContent content(ByteBuf data) {
    return new DefaultHttpContent(data);
  }
  
  public static HttpContent contentString(String data, Charset cs) {
    return new DefaultHttpContent(Unpooled.copiedBuffer(data, cs));
  }
  
  public static HttpContent contentUtf8(String data) {
    return contentString(data, StandardCharsets.UTF_8);
  }
  
  public static LastHttpContent lastContent(ByteBuf data) {
    return new DefaultLastHttpContent(data);
  }
  
  public static LastHttpContent lastContentString(String data, Charset cs) {
    return new DefaultLastHttpContent(Unpooled.copiedBuffer(data, cs));
  }
  
  public static LastHttpContent lastContentUtf8(String data) {
    return lastContentString(data, StandardCharsets.UTF_8);
  }
  
  
  
  
  
  public static abstract class HttpMessageBuilder {
    protected final HttpVersion version;
    protected final HttpHeaders headers;
    protected final ByteBuf body;
    protected final Function<? extends HttpMessage,HttpExchange> apply;
    protected final boolean last;
    
    protected HttpMessageBuilder(HttpVersion version, HttpHeaders headers, ByteBuf body, Function<? extends HttpMessage,HttpExchange> apply, boolean last) {
      this.version = version;
      this.body = body;
      this.headers = headers;
      this.apply = apply;
      this.last = last;
    }
    protected HttpMessageBuilder(Function<? extends HttpMessage,HttpExchange> apply) {
      this(HttpVersion.HTTP_1_1, new DefaultHttpHeaders(), null, apply, false);
    }
    protected HttpMessageBuilder() {
      this(HttpVersion.HTTP_1_1, new DefaultHttpHeaders(), null, null, false);
    }
    
    public HttpVersion version() { return version; }
    public abstract HttpMessageBuilder version(HttpVersion v);
    
    public HttpHeaders headers() { return headers; }
    public abstract HttpMessageBuilder headers(HttpHeaders h);
  
    public HttpMessageBuilder addHeader(AsciiString name, Object value) {
      if(name != null && value != null) {
        headers.add(name, value);
      }
      return this;
    }
    public HttpMessageBuilder addHeader(String name, Object value) {
      if(name != null && value != null) {
        headers.add(name, value);
      }
      return this;
    }
    
    public ByteBuf body() { return body; }
    public abstract HttpMessageBuilder body(ByteBuf body);
    public abstract HttpMessageBuilder bodyUtf8(String body);
    public abstract HttpMessageBuilder bodyJson(String body);
    public abstract HttpMessageBuilder bodyString(String body, Charset cs);
    
    public boolean isLastContent() { return last; }
    public abstract HttpMessageBuilder lastContent();
    
    
    public <M extends HttpMessage> M build() {
      return (M) new DefaultHttpMessage(version, headers){};
    }
    
    public HttpExchange done() {
      return Objects.requireNonNull(apply, "Bad null Function<HttpMessage,HttpExchange>").apply(build());
    }
  }
  
  
  
  public static class HttpRequestBuilder extends HttpMessageBuilder {
    private final HttpMethod method;
    private final String uri;
    private final Map<String,Object> query;
    
    public HttpRequestBuilder(HttpVersion version, HttpMethod method, HttpHeaders headers, String uri, Map<String,Object> query, ByteBuf body, Function<? extends HttpMessage,HttpExchange> apply, boolean last) {
      super(version, headers, body, apply, last);
      this.method = method;
      this.uri = uri;
      this.query = query;
    }
    public HttpRequestBuilder(Function<? extends HttpMessage,HttpExchange> apply) {
      this(HttpVersion.HTTP_1_1, null, new DefaultHttpHeaders(), null, new HashMap(), null, apply, false);
    }
    public HttpRequestBuilder() {
      this(HttpVersion.HTTP_1_1, null, new DefaultHttpHeaders(), null, new HashMap(), null, null, false);
    }
    
    @Override
    public HttpRequestBuilder version(HttpVersion version) {
      return new HttpRequestBuilder(version, method, headers, uri, query, body, apply, last);
    }
    
    public HttpMethod method() { return method; }
    public HttpRequestBuilder method(HttpMethod method) {
      return new HttpRequestBuilder(version, method, headers, uri, query, body, apply, last);
    }
    
    public HttpRequestBuilder get() {
      return new HttpRequestBuilder(version, HttpMethod.GET, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder get(String uri) {
      return new HttpRequestBuilder(version, HttpMethod.GET, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder post() {
      return new HttpRequestBuilder(version, HttpMethod.POST, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder post(String uri) {
      return new HttpRequestBuilder(version, HttpMethod.POST, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder patch() {
      return new HttpRequestBuilder(version, HttpMethod.PATCH, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder patch(String uri) {
      return new HttpRequestBuilder(version, HttpMethod.PATCH, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder put() {
      return new HttpRequestBuilder(version, HttpMethod.PUT, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder put(String uri) {
      return new HttpRequestBuilder(version, HttpMethod.PUT, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder delete() {
      return new HttpRequestBuilder(version, HttpMethod.DELETE, headers, uri, query, body, apply, last);
    }

    public HttpRequestBuilder delete(String uri) {
      return new HttpRequestBuilder(version, HttpMethod.DELETE, headers, uri, query, body, apply, last);
    }

    @Override
    public HttpRequestBuilder addHeader(AsciiString name, Object value) {
      super.addHeader(name, value);
      return this;
    }
    
    @Override
    public HttpRequestBuilder addHeader(String name, Object value) {
      super.addHeader(name, value);
      return this;
    }
    
    @Override
    public HttpRequestBuilder headers(HttpHeaders headers) { 
      headers().add(headers);
      return this;
    }
    
    public String uri() { return uri; }
    public HttpRequestBuilder uri(String uri) {
      return new HttpRequestBuilder(version, method, headers, uri, query, body, apply, last);
    }
    
    @Override
    public HttpRequestBuilder body(ByteBuf body) {
      HttpRequestBuilder b = this;
      if(body != null) {
        b = new HttpRequestBuilder(version, method, 
            headers.add(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes()), 
            uri, query, body, apply, last
        );
      }
      return b;
    }
    
    @Override
    public HttpRequestBuilder bodyUtf8(String body) {
      HttpRequestBuilder b = this;
      if(body != null) {
        b = bodyString(body, StandardCharsets.UTF_8);
      }
      return b;
    }
    
    @Override
    public HttpRequestBuilder bodyString(String body, Charset cs) {
      HttpRequestBuilder b = this;
      if(body != null) {
        b = body(Unpooled.copiedBuffer(body, cs));
      }
      return b;
    }
    
    @Override
    public HttpRequestBuilder bodyJson(String body) {
      HttpRequestBuilder b = this;
      if(body != null) {
        headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        b = bodyUtf8(body);
      }
      return b;
    }
    
    @Override
    public HttpRequestBuilder lastContent() {
      return new HttpRequestBuilder(version, method, headers, uri, query, body, apply, true);
    }
    
    public Map<String,Object> queryParams() { return query; }
    public HttpRequestBuilder addQueryParam(String par, Object val) {
      if(par != null && !par.isBlank() && val != null) {
        query.put(par, val);
      }
      return this;
    }
    
    @Override
    public HttpRequest build() {
      Objects.requireNonNull(version(), "Bad null HttpVersion");
      Objects.requireNonNull(method, "Bad null HttpMethod");
      Objects.requireNonNull(uri, "Bad null uri");
      String suri = Objects.requireNonNull(uri, "Bad null uri");
      System.out.println(query);
      if(!query.isEmpty()) {
        QueryStringEncoder enc = new QueryStringEncoder(suri, StandardCharsets.UTF_8);
        query.entrySet().forEach(e->enc.addParam(e.getKey(), Objects.toString(e.getValue())));
        suri = enc.toString();
      }
      if(body() != null) {
        return new DefaultFullHttpRequest(version, method, suri, body, headers, EmptyHttpHeaders.INSTANCE);
      }
      else if(last) {
        DefaultFullHttpRequest r = new DefaultFullHttpRequest(version, method, suri);
        r.headers().add(headers);
        return r;
      }
      else {
        return new DefaultHttpRequest(version, method, suri, headers);
      }
    }
  }
  
  
  
  public static class HttpResponseBuilder extends HttpMessageBuilder {
    private final HttpResponseStatus status;
    
    public HttpResponseBuilder(HttpVersion version, HttpResponseStatus status, HttpHeaders headers, ByteBuf body, Function<? extends HttpMessage,HttpExchange> apply, boolean last) {
      super(version, headers, body, apply, last);
      this.status = status;
    }
    
    public HttpResponseBuilder(Function<? extends HttpMessage,HttpExchange> apply) {
      this(HttpVersion.HTTP_1_1, null, new DefaultHttpHeaders(), null, apply, false);
    }
    
    public HttpResponseBuilder() {
      this(HttpVersion.HTTP_1_1, null, new DefaultHttpHeaders(), null, null, false);
    }
    
    @Override
    public HttpResponseBuilder version(HttpVersion version) {
      return new HttpResponseBuilder(version, status, headers, body, apply, last);
    }
    
    public HttpResponseStatus status() { return status; }
    public HttpResponseBuilder status(HttpResponseStatus status) {
      return new HttpResponseBuilder(version, status, headers, body, apply, last);
    }
    
    public HttpResponseBuilder ok() {
      return new HttpResponseBuilder(version, HttpResponseStatus.OK, headers, body, apply, last);
    }
    
    public HttpResponseBuilder badRequest() {
      return new HttpResponseBuilder(version, HttpResponseStatus.BAD_REQUEST, headers, body, apply, last);
    }
    
    public HttpResponseBuilder noContet() {
      return new HttpResponseBuilder(version, HttpResponseStatus.NO_CONTENT, headers, body, apply, last);
    }
    
    public HttpResponseBuilder notFound() {
      return new HttpResponseBuilder(version, HttpResponseStatus.NOT_FOUND, headers, body, apply, last);
    }
    
    public HttpResponseBuilder notModified() {
      return new HttpResponseBuilder(version, HttpResponseStatus.NOT_MODIFIED, headers, body, apply, last);
    }
    
    public HttpResponseBuilder unauthorized() {
      return new HttpResponseBuilder(version, HttpResponseStatus.UNAUTHORIZED, headers, body, apply, last);
    }
    
    @Override
    public HttpResponseBuilder addHeader(AsciiString name, Object value) {
      super.addHeader(name, value);
      return this;
    }
    
    @Override
    public HttpResponseBuilder addHeader(String name, Object value) {
      super.addHeader(name, value);
      return this;
    }
    
    @Override
    public HttpResponseBuilder headers(HttpHeaders headers) { 
      headers().add(headers);
      return this;
    }
    
    @Override
    public HttpResponseBuilder body(ByteBuf body) {
      HttpResponseBuilder b = this;
      if(body != null) {
        b = new HttpResponseBuilder(version, status, 
          headers.add(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes()), 
          body, apply, last
      );
      }
      return b;
    }
    
    @Override
    public HttpResponseBuilder bodyString(String body, Charset cs) {
      HttpResponseBuilder b = this;
      if(body != null) {
        b = body(Unpooled.copiedBuffer(body, cs));
      }
      return b;
    }
    
    @Override
    public HttpResponseBuilder bodyUtf8(String body) {
      HttpResponseBuilder b = this;
      if(body != null) {
        b = bodyString(body, StandardCharsets.UTF_8);
      }
      return b;
    }
    
    @Override
    public HttpResponseBuilder bodyJson(String body) {
      HttpResponseBuilder b = this;
      if(body != null) {
        headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        b = bodyUtf8(body);
      }
      return b;
    }
    
    @Override
    public HttpResponseBuilder lastContent() {
      return new HttpResponseBuilder(version, status, headers, body, apply, true);
    }
    
    @Override
    public HttpResponse build() {
      Objects.requireNonNull(version(), "Bad null HttpVersion");
      Objects.requireNonNull(status, "Bad null HttpResponseStatus");
      if(body() != null) {
        return new DefaultFullHttpResponse(version, status, body, headers, EmptyHttpHeaders.INSTANCE);
      }
      else if(last) {
        DefaultFullHttpResponse r = new DefaultFullHttpResponse(version, status);
        r.headers().add(headers);
        return r;
      }
      else {
        return new DefaultHttpResponse(version, status, headers);
      }
    }
  }
  
}
