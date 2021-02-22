/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.util.Optional;
import java.util.function.Supplier;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class SupplierConditional<T> extends Conditional<Void,T> implements Supplier<Optional<T>> {
  
  protected SupplierConditional(Supplier<Boolean> test, Supplier<T> sup, Optional<Conditional<Void,T>> els) {
    super(x->test.get(), x->sup.get(), els);
  }
  
  public SupplierConditional(Supplier<Boolean> test, Supplier<T> sup) {
    this(test, sup, Optional.empty());
  }
  
  public static <X> SupplierConditional<X> of(Supplier<Boolean> s, Supplier<X> sup) {
    return new SupplierConditional(s, sup);
  }
  
  public static SupplierConditional<Void> of(Supplier<Boolean> s) {
    return of(s, ()->null);
  }
  
  public boolean test() {
    return super.test(null);
  }
  
  @Override
  public Optional<T> get() {
    return super.apply(null);
  }
  
  public <X> SupplierConditional<X> supply(Supplier<X> sup) {
    return new SupplierConditional(this::test, sup, elseopt);
  }
  
  public SupplierConditional<T> elseIf(SupplierConditional<T> cnd) {
    Conditional<Void,T> nc = super.elseIf((Conditional<Void,T>)cnd);
    return new SupplierConditional(()->nc.test(null), ()->nc.apply(null).get(), Optional.of(nc));
  }
  
  public SupplierConditional elseIf(Supplier<Boolean> s, Supplier<T> sup) {
    return elseIf(new SupplierConditional(s, sup));
  }
  
  public SupplierConditional elseThen(Supplier<T> sup) {
    return elseIf(new SupplierConditional(()->true, sup));
  }
  
  public SupplierConditional elseThrow(Supplier<Throwable> s) {
    return elseIf(new SupplierConditional(()->true, ()->{throw Unchecked.unchecked(s.get());}));
  }
  
}
