/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.util.function.Predicate;


/**
 *
 * @author Juno
 */
@FunctionalInterface
public interface VoidPredicate extends Predicate<Void> {
  
  public boolean test();
  
  @Override
  public default boolean test(Void v) {
    return test();
  }
  
}
