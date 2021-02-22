/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import net.jun0rr.doxy.http.HttpRoute;


/**
 *
 * @author Juno
 */
@FunctionalInterface
public interface Routable {
  
  public boolean match(HttpRoute r);
  
}
