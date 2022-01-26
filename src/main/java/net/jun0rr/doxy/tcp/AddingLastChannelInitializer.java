/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import us.pserver.tools.Hash;


/**
 *
 * @author juno
 */
public class AddingLastChannelInitializer extends ChannelInitializer<SocketChannel> {
  
  private final Collection<Supplier<ChannelHandler>> handlers;
  
  private final Optional<SSLHandlerFactory> sslFactory;
  
  public AddingLastChannelInitializer(Optional<SSLHandlerFactory> sslFactory, Collection<Supplier<ChannelHandler>> handlers) {
    this.handlers = Collections.unmodifiableCollection(
        Objects.requireNonNull(handlers, "Bad null ChannelHandler Collection")
    );
    if(handlers.isEmpty()) {
      throw new IllegalArgumentException("ChannelHandler Collection is Empty!");
    }
    this.sslFactory = Objects.requireNonNull(sslFactory, "Bad null SSLHandlerFactory Optional");
  }
  
  @Override
  protected void initChannel(SocketChannel c) throws Exception {
    //c.pipeline().addLast(hexDump());
    sslFactory.ifPresent(f->c.pipeline().addLast(f.create(c.alloc())));
    handlers.stream().forEach(s->c.pipeline().addLast(s.get()));
  }
  
  private ChannelInboundHandler hexDump() {
    return new ChannelInboundHandlerAdapter() {
      @Override 
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        int ridx = buf.readerIndex(), widx = buf.writerIndex();
        byte[] bs = new byte[buf.readableBytes()];
        buf.readBytes(bs);
        buf.readerIndex(ridx).writerIndex(widx);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[HEX_DUMP] Message Received: (%s -> %s) %d bytes%n", ctx.channel().remoteAddress(), ctx.channel().localAddress(), buf.readableBytes()));
        sb.append(String.format("[HEX_DUMP] Hex: %s%n", Hash.bytesToHex(bs)));
        if(new String(bs, 1, 5, StandardCharsets.UTF_8).matches("[a-zA-Z0-9]+.*")) {
          sb.append(String.format("[HEX_DUMP] Priteable msg: %s%n", new String(bs, StandardCharsets.UTF_8)));
        }
        sb.append("[HEX_DUMP] -------------------------------------");
        System.out.println(sb.toString());
        ctx.fireChannelRead(msg);
      }
    };
  }
  
}
