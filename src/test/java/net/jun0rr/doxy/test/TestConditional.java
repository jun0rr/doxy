/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Optional;
import net.jun0rr.doxy.common.Conditional;
import net.jun0rr.doxy.common.ConsumerConditional;
import net.jun0rr.doxy.common.InstanceOf;
import net.jun0rr.doxy.common.SupplierConditional;
import net.jun0rr.doxy.common.VoidConditional;
import net.jun0rr.doxy.http.HttpRequest;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestConditional {
  @Test
  public void conditional() {
    System.out.println("---- conditional ----");
    Object msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    msg = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    msg = "/hello";
    //msg = null;
    Optional<HttpRequest> req = Conditional.of(o->o instanceof HttpRequest)
        .map(o->{
          System.out.println("-> o instanceof HttpRequest");
          return (HttpRequest)o;
        })
        .elseIf(o->o instanceof FullHttpRequest, o->{
          System.out.println("-> o instanceof FullHttpRequest");
          return HttpRequest.of((FullHttpRequest)o);
        })
        .elseIf(o->o instanceof String, o->{
          System.out.println("-> o instanceof String");
          return HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, (String)o);
        })
        .elseThrow(o->new IllegalArgumentException("Bad object type: " + o))
        .apply(msg)
        ;
    System.out.println(req);
  }
  
  @Test
  public void ifInstance() {
    System.out.println("---- ifInstance ----");
    Object msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    msg = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    msg = "/hello";
    //msg = null;
    Optional<HttpRequest> req = InstanceOf.of(HttpRequest.class, o->{
          System.out.println("-> o instanceof HttpRequest");
          return o;
        })
        .elseOf(FullHttpRequest.class, o->{
          System.out.println("-> o instanceof FullHttpRequest");
          return HttpRequest.of(o);
        })
        .elseOf(String.class, o->{
          System.out.println("-> o instanceof String");
          return HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, o);
        })
        .elseThrow(o->new IllegalArgumentException("Bad object type: " + o))
        .apply(msg)
        ;
    System.out.println(req);
  }
  
  @Test
  public void voidConditional() {
    System.out.println("---- voidConditional ----");
    Object msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    //Object msg = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    //Object msg = "/hello";
    //Object msg = null;
    VoidConditional v = VoidConditional.of(()->msg instanceof HttpRequest)
        .run(()->System.out.println("-> o instanceof HttpRequest"))
        .elseIf(()->msg instanceof FullHttpRequest, ()->System.out.println("-> o instanceof FullHttpRequest"))
        .elseIf(()->msg instanceof String, ()->System.out.println("-> o instanceof String"))
        .elseThrow(()->new IllegalArgumentException("Bad object msg"))
        ;
    System.out.println(v.test());
    v.run();
  }
  
  @Test
  public void supplierConditional() {
    System.out.println("---- supplierConditional ----");
    //Object msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    Object msg = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    //Object msg = "/hello";
    //Object msg = null;
    SupplierConditional v = SupplierConditional.of(()->msg instanceof HttpRequest)
        .supply(()->{
          System.out.println("-> o instanceof HttpRequest");
          return (HttpRequest)msg;
        })
        .elseIf(()->msg instanceof FullHttpRequest, ()->{
          System.out.println("-> o instanceof FullHttpRequest");
          return HttpRequest.of((FullHttpRequest)msg);
        })
        .elseIf(()->msg instanceof String, ()->{
          System.out.println("-> o instanceof String");
          return HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, (String)msg);
        })
        .elseThrow(()->new IllegalArgumentException("Bad object type: " + msg))
        ;
    System.out.println(v.get());
  }
  
  @Test
  public void consumerConditional() {
    System.out.println("---- consumerConditional ----");
    //Object msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    Object msg = HttpRequest.of(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    //Object msg = "/hello";
    //Object msg = null;
    ConsumerConditional v = ConsumerConditional.eval(o->o instanceof HttpRequest)
        .consume(o->System.out.println("-> o instanceof HttpRequest"))
        .elseIf(o->o instanceof FullHttpRequest, o->{
          System.out.println("-> o instanceof FullHttpRequest");
        })
        .elseIf(o->o instanceof String, o->{
          System.out.println("-> o instanceof String");
        })
        .elseThrow(o->new IllegalArgumentException("Bad object type: " + o))
        ;
    v.accept(msg);
  }
}
