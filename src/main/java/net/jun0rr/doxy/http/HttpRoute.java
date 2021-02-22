/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 *
 * @author Juno
 */
public interface HttpRoute extends Routable {
  
  public String uri();
  
  public List<HttpMethod> methods();
  
  @Override
  public boolean match(HttpRoute r);
  
  public boolean match(String uri, HttpMethod... mts);
  
  
  
  public static HttpRoute of(String uri, HttpMethod... mts) {
    return new HttpRouteImpl(uri, mts);
  }
  
  public static HttpRoute get(String uri) {
    return new HttpRouteImpl(uri, HttpMethod.GET);
  }
  
  public static HttpRoute post(String uri) {
    return new HttpRouteImpl(uri, HttpMethod.POST);
  }
  
  public static HttpRoute put(String uri) {
    return new HttpRouteImpl(uri, HttpMethod.PUT);
  }
  
  public static HttpRoute delete(String uri) {
    return new HttpRouteImpl(uri, HttpMethod.DELETE);
  }
  
  public static HttpRoute of(HttpRequest req) {
    return new HttpRouteImpl(req.uri(), req.method());
  }
  
  
  
  
  
  public static class HttpRouteImpl implements HttpRoute {
    
    private final List<HttpMethod> methods;
    
    private final String uri;
    
    public HttpRouteImpl(String uri, HttpMethod... mts) {
      this.methods = Arrays.asList(Objects.requireNonNull(mts, "Bad null HttpMethods"));
      this.uri = Objects.requireNonNull(uri, "Bad null uri string");
    }
    
    public HttpRouteImpl(String uri, Collection<HttpMethod> mts) {
      this.methods = List.copyOf(Objects.requireNonNull(mts, "Bad null HttpMethod List"));
      this.uri = Objects.requireNonNull(uri, "Bad null uri string");
    }
    
    @Override
    public String uri() {
      return uri;
    }
    
    @Override
    public List<HttpMethod> methods() {
      return methods;
    }
    
    @Override
    public boolean match(HttpRoute r) {
      //System.out.printf("HttpRoute.match( %s ):%n", r);
      //System.out.printf("  - '%s'.matches( '%s' ): %s%n", this.uri, );
      return methods.stream()
          //.peek(m->System.out.printf("  - %s.equals", m))
          .anyMatch(m->
          r.methods().stream()
              //.peek(n->System.out.printf("( %s ): %s%n", n, m.equals(n)))
              .anyMatch(m::equals)) 
          && (this.uri.equals(r.uri()) 
          || this.uri.matches(r.uri())
          || r.uri().matches(this.uri)); 
    }
    
    @Override
    public boolean match(String uri, HttpMethod... mts) {
      return match(HttpRoute.of(uri, mts));
    }
    
    @Override
    public int hashCode() {
      int hash = 7;
      hash = 41 * hash + Objects.hashCode(this.methods);
      hash = 41 * hash + Objects.hashCode(this.uri);
      return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
      return HttpRoute.class.isAssignableFrom(obj.getClass()) && this.match((HttpRoute) obj);
    }


    @Override
    public String toString() {
      return "HttpRoute{uri=" + uri + ", method=" + methods + '}';
    }
    
  }
  
}
