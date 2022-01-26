/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Objects;


/**
 *
 * @author Juno
 */
public class HttpExceptionalResponse extends DefaultHttpResponse {
  
  public HttpExceptionalResponse(HttpResponseStatus s, Throwable t) {
    super(HttpVersion.HTTP_1_1, s);
    Objects.requireNonNull(t, "Bad null Throwable");
    headers().set("x-error-type", t.getClass());
    if(t.getMessage() != null && !t.getMessage().isBlank()) {
      headers().set("x-error-message", t.getMessage());
    }
    if(t.getCause() != null) {
      headers().set("x-error-cause", Objects.toString(t.getCause()));
    }
    StackTraceElement[] elts = t.getStackTrace();
    for(int i = 0; i < Math.min(3, elts.length); i++) {
      headers().set("x-error-trace-" + i, elts[i]);
    }
    headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
  }
  
  public HttpExceptionalResponse(HttpResponseStatus s, String msg, String cause, StackTraceElement[] stack) {
    super(HttpVersion.HTTP_1_1, s);
    headers().set("x-error-message", Objects.requireNonNull(msg, "Bad null message string"));
    Objects.requireNonNull(stack, "Bad null StackTrace");
    if(cause != null) {
      headers().set("x-error-cause", cause);
    }
    for(int i = 0; i < Math.min(3, stack.length); i++) {
      headers().set("x-error-trace-" + i, stack[i]);
    }
    headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
  }
  
  public HttpExceptionalResponse(HttpResponseStatus s, String msg, String cause) {
    super(HttpVersion.HTTP_1_1, s);
    headers().set("x-error-message", Objects.requireNonNull(msg, "Bad null message string"));
    if(cause != null) {
      headers().set("x-error-cause", cause);
    }
    headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
  }
  
  public HttpExceptionalResponse(HttpResponseStatus s, String msg) {
    super(HttpVersion.HTTP_1_1, s);
    headers().set("x-error-message", Objects.requireNonNull(msg, "Bad null message string"));
    headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
  }
  
  public static HttpResponse of(HttpResponseStatus s, Throwable t) {
    return new HttpExceptionalResponse(s, t);
  }
  
  public static HttpResponse of(HttpResponseStatus s, String msg, String cause, StackTraceElement[] stack) {
    return new HttpExceptionalResponse(s, msg, cause, stack);
  }
  
  public static HttpResponse of(HttpResponseStatus s, String msg, String cause) {
    return new HttpExceptionalResponse(s, msg, cause);
  }
  
  public static HttpResponse of(HttpResponseStatus s, String msg) {
    return new HttpExceptionalResponse(s, msg);
  }
  
}
