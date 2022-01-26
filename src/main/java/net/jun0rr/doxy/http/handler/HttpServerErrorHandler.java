/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import net.jun0rr.doxy.http.HttpExchange;


/**
 *
 * @author Juno
 */
public class HttpServerErrorHandler implements BiFunction<HttpExchange,Throwable,Optional<HttpResponse>> {
  
  public HttpServerErrorHandler() {}
  
  @Override
  public Optional<HttpResponse> apply(HttpExchange x, Throwable t) {
    t.printStackTrace();
    HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    res.headers().set("x-error-type", t.getClass());
    if(t.getMessage() != null && !t.getMessage().isBlank()) {
      res.headers().set("x-error-message", t.getMessage());
    }
    if(t.getCause() != null) {
      res.headers().set("x-error-cause", Objects.toString(t.getCause()));
    }
    StackTraceElement[] elts = t.getStackTrace();
    for(int i = 0; i < Math.min(3, elts.length); i++) {
      res.headers().set("x-error-trace-" + i, elts[i]);
    }
    res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
    return Optional.of(res);
  }
  
}
