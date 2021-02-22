/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCounted;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.common.MessageContainer;


/**
 *
 * @author Juno
 */
public interface HttpResponse extends io.netty.handler.codec.http.HttpResponse, MessageContainer, ReferenceCounted {

  @Override public HttpResponse withMessage(Object msg);
  
  @Override public Optional<HttpResponse> forward();
  
  @Override public Optional<HttpResponse> empty();
  
  public void dispose();
  
  
  
  public static HttpResponse of(HttpVersion vrs, HttpResponseStatus sts, HttpHeaders hds, Object msg) {
    return new HttpResponseImpl(vrs, sts, hds, msg);
  }
  
  public static HttpResponse of(HttpVersion vrs, HttpResponseStatus sts, HttpHeaders hds) {
    return new HttpResponseImpl(vrs, sts, hds);
  }
  
  public static HttpResponse of(HttpVersion vrs, HttpResponseStatus sts) {
    return new HttpResponseImpl(vrs, sts);
  }
  
  public static HttpResponse of(HttpResponseStatus sts, Object msg) {
    return new HttpResponseImpl(sts, msg);
  }
  
  public static HttpResponse of(HttpResponseStatus sts) {
    return new HttpResponseImpl(sts);
  }
  
  public static HttpResponse of(FullHttpResponse fr) {
    HttpResponse res = of(fr.protocolVersion(), fr.status(), fr.headers(), fr.content());
    if(!fr.trailingHeaders().isEmpty()) {
      res.headers().setAll(fr.trailingHeaders());
    }
    return res;
  }
  
  
  
  
  
  public static class HttpResponseImpl extends DefaultFullHttpResponse implements HttpResponse {

    private final Object msg;
    
    public HttpResponseImpl(HttpVersion version, HttpResponseStatus status, HttpHeaders headers, Object msg) {
      super(version, status, (msg instanceof ByteBuf) ? (ByteBuf)msg : Unpooled.EMPTY_BUFFER, headers, EmptyHttpHeaders.INSTANCE);
      this.msg = msg;
    }
    
    public HttpResponseImpl(HttpVersion version, HttpResponseStatus status, HttpHeaders headers) {
      this(version, status, headers, null);
    }
    
    public HttpResponseImpl(HttpVersion version, HttpResponseStatus status) {
      super(version, status);
      this.msg = null;
    }
    
    public HttpResponseImpl(HttpResponseStatus status, Object msg) {
      super(HttpVersion.HTTP_1_1, status);
      this.msg = msg;
    }
    
    public HttpResponseImpl(HttpResponseStatus status) {
      this(HttpVersion.HTTP_1_1, status);
    }
    
    @Override
    public <T> T message() {
      return (T) (msg != null ? msg : this.content());
    }
    
    @Override
    public HttpResponse withMessage(Object msg) {
      dispose();
      return new HttpResponseImpl(protocolVersion(), status(), headers(), msg);
    }
    
    @Override
    public void dispose() {
      if(this.refCnt() > 0) this.release(refCnt());
      if(this.msg != null && this.msg instanceof ReferenceCounted) {
        ReferenceCounted ref = (ReferenceCounted)this.msg;
        if(ref.refCnt() > 0) ref.release(ref.refCnt());
      }
    }
    
    @Override
    public Optional<HttpResponse> forward() {
      return Optional.of(this);
    }
    
    @Override
    public Optional<HttpResponse> empty() {
      return Optional.empty();
    }
    
    @Override
    public int hashCode() {
      int hash = 7;
      hash = 59 * hash + Objects.hashCode(this.protocolVersion());
      hash = 59 * hash + Objects.hashCode(this.status());
      hash = 59 * hash + Objects.hashCode(this.headers());
      return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (!HttpResponse.class.isAssignableFrom(obj.getClass())) {
        return false;
      }
      final HttpResponse other = (HttpResponse) obj;
      if(!Objects.equals(this.protocolVersion(), other.protocolVersion())) {
        return false;
      }
      if(!Objects.equals(this.status(), other.status())) {
        return false;
      }
      return Objects.equals(this.headers(), other.headers());
    }
    
    @Override
    public String toString() {
      return "HttpResponse{" + "version=" + protocolVersion() + ", status=" + status() + '}';
    }
    
  }
  
}
