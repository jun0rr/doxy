/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http.handler;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpRoute;


/**
 *
 * @author Juno
 */
public class HttpRouteHandler implements HttpHandler {
  
  private final List<RoutableHttpHandler> handlers;
  
  private final HttpHandler defaultHandler;
  
  public HttpRouteHandler(HttpHandler defaultHandler, Collection<RoutableHttpHandler> handlers) {
    this.handlers = List.copyOf(Objects.requireNonNull(handlers, "Bad null RoutableHttpHandler Collection"));
    this.defaultHandler = Objects.requireNonNull(defaultHandler, "Bad null default HttpHandler");
  }
  
  public HttpRouteHandler(Collection<RoutableHttpHandler> handlers) {
    this(BAD_REQUEST, handlers);
  }
  
  private Iterator<HttpHandler> getHandlers(HttpRoute r) {
    Iterator<HttpHandler> it = handlers.stream()
        .filter(h->h.match(r))
        //.peek(h->System.out.printf("PASSED %s -> %s%n", r, h.route()))
        .map(h->(HttpHandler)h)
        .sorted()
        .iterator();
    if(!it.hasNext()) {
      it = List.of(defaultHandler).iterator();
    }
    return it;
  }
  
  @Override
  public Optional<? extends HttpExchange> apply(HttpExchange he) throws Exception {
    Iterator<HttpHandler> it = getHandlers(HttpRoute.of(he.request()));
    Optional<? extends HttpExchange> optx = Optional.of(he);
    while(it.hasNext() && optx.isPresent()) {
      HttpHandler h = it.next();
      //System.out.println("[HttpRouteHandler.apply] handler=" + ((RoutableHttpHandler)h).route());
      optx = h.apply(optx.get());
    }
    return optx;
  }
  
  @Override
  public String toString() {
    return "HttpRouteHandler{" + "default=" + defaultHandler + ", handlers=" + handlers + '}';
  }
  
}
