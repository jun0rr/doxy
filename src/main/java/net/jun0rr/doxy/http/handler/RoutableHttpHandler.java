/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import net.jun0rr.doxy.http.Routable;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRoute;


/**
 *
 * @author juno
 */
public class RoutableHttpHandler implements HttpHandler, Routable, Comparable<RoutableHttpHandler> {
  
  protected final HttpRoute route;

  private final HttpHandler handler;

  public RoutableHttpHandler(HttpRoute hrt, HttpHandler hnd) {
    this.route = Objects.requireNonNull(hrt, "Bad null HttpRoute");
    this.handler = Objects.requireNonNull(hnd, "Bad null HttpHandler");
  }

  public static RoutableHttpHandler of(HttpRoute r, HttpHandler h) {
    return new RoutableHttpHandler(r, h);
  }
  
  public HttpRoute route() {
    return route;
  }
  
  @Override
  public Optional<? extends HttpExchange> apply(HttpExchange he) throws Exception {
    return handler.apply(he);
  }

  @Override
  public boolean match(HttpRoute r) {
    return route.match(r);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + Objects.hashCode(this.route);
    hash = 17 * hash + Objects.hashCode(this.handler);
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
    if (RoutableHttpHandler.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    final RoutableHttpHandler other = (RoutableHttpHandler) obj;
    return other.match(route);
  }
  
  @Override
  public int compareTo(RoutableHttpHandler o) {
    return (-1) * route.uri().compareToIgnoreCase(o.route().uri());
  }

  @Override
  public String toString() {
    return "RoutableHttpHandler{" + "route=" + route + ", handler=" + handler + '}';
  }
  
}
