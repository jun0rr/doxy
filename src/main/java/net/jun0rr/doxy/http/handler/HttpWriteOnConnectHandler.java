/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import net.jun0rr.doxy.tcp.handler.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Objects;
import java.util.Queue;
import net.jun0rr.doxy.tcp.TcpChannel;


/**
 *
 * @author Juno
 */
public class HttpWriteOnConnectHandler extends ChannelInboundHandlerAdapter {
  
  private final TcpChannel channel;
  
  public HttpWriteOnConnectHandler(TcpChannel ch) {
    this.channel = Objects.requireNonNull(ch, "Bad null TcpChannel");
  }
  
  @Override 
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    SslHandler ssl = ctx.pipeline().get(SslHandler.class);
    final Queue messages = channel.context().messages();
    GenericFutureListener write = f->{
      Future ctf = channel.context().future();
      Object msg = messages.poll();
      while(msg != null) {
        Object nextmsg = messages.poll();
        if(nextmsg == null) {
          ctx.writeAndFlush(msg);
          if(ctf instanceof Promise && !ctf.isDone()) {
            ((Promise)ctf).setSuccess(null);
          }
        }
        else {
          ctx.write(msg);
        }
        msg = nextmsg;
      }
      ctx.fireChannelActive();
    };
    if(ssl != null) {
      ssl.handshakeFuture().addListener(write);
    }
    else {
      ctx.newSucceededFuture().addListener(write);
    }
  }
  
}
