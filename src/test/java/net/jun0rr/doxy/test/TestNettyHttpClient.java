/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import us.pserver.tools.Unchecked;


/**
 *
 * @author juno
 */
public class TestNettyHttpClient {
  /*
  @Test
  public void method() {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.option(ChannelOption.TCP_NODELAY, true)
          .group(group)
          .channel(NioSocketChannel.class)
          .handler(new HttpInitHandler());
      //Channel c = b.connect("disec6.intranet.bb.com.br", 443).sync().channel();
      Channel c = b.connect("127.0.0.1", 40080).sync().channel();
      //Channel c = b.connect("www.google.com", 80).sync().channel();
      HttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://www.google.com/");
      req.headers().set(HttpHeaderNames.ACCEPT, "text/html")
          //.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP_DEFLATE)
          .set(HttpHeaderNames.HOST, "www.google.com")
          .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
          .set("Proxy-Connection", HttpHeaderValues.KEEP_ALIVE)
          .set(HttpHeaderNames.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:62.0) Gecko/20100101 Firefox/62.0")
          .set(HttpHeaderNames.PROXY_AUTHORIZATION, "Basic ZjYwMzY0Nzc6OTg3NDEwMjU=");
      c.writeAndFlush(req);
      c.closeFuture().sync();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw Unchecked.unchecked(e);
    }
    finally {
      group.shutdownGracefully();
    }
  }
  
  
  
  static class HttpResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext chc, Object o) throws Exception {
      FullHttpResponse res = (FullHttpResponse) o;
      System.out.println(res.status());
      System.out.println("-------------");
      res.headers().forEach(e->System.out.println("  " + e.getKey() + ": " + e.getValue()));
      System.out.println("-------------");
      System.out.println(res.content().toString(StandardCharsets.UTF_8));
      System.out.println("-------------");
    }

  }
  
  
  
  static class HttpInitHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel c) throws Exception {
      SslContext ssl = SslContextBuilder.forClient()
          .trustManager(InsecureTrustManagerFactory.INSTANCE)
          .build();
      
      //c.pipeline().addLast(ssl.newHandler(c.alloc()));
      //c.pipeline().addLast(new HttpProxyHandler(new InetSocketAddress("localhost", 40080), "f6036477", "98741025"));
      c.pipeline().addLast(new HttpClientCodec());
      c.pipeline().addLast(new HttpContentDecompressor());
      c.pipeline().addLast(new HttpObjectAggregator(1024*1024));
      c.pipeline().addLast(new HttpResponseHandler());
    }
    
  }
  */
}
