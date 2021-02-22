/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.jun0rr.doxy.tcp.ChannelHandler;



/**
 *
 * @author juno
 */
public interface HttpHandler extends ChannelHandler<HttpExchange> {
  
  public static final HttpHandler BAD_REQUEST = x -> {
    x.response().setStatus(HttpResponseStatus.BAD_REQUEST);
    x.response().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
    return x.sendAndClose();
  };
  
}
