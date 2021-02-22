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
public class VoidConditional extends Conditional<Void,Void> implements Runnable {
  
  protected VoidConditional(Supplier<Boolean> test, Runnable rn, Optional<Conditional<Void,Void>> els) {
    super(x->test.get(), x->{rn.run(); return null;}, els);
  }
  
  public VoidConditional(Supplier<Boolean> test, Runnable rn) {
    super(x->test.get(), x->{rn.run(); return null;}, Optional.empty());
  }
  
  public static VoidConditional of(Supplier<Boolean> s, Runnable r) {
    return new VoidConditional(s, r);
  }
  
  public static VoidConditional of(Supplier<Boolean> s) {
    return of(s, ()->{});
  }
  
  public boolean test() {
    return super.test(null);
  }
  
  @Override
  public void run() {
    super.apply(null);
  }
  
  public VoidConditional run(Runnable r) {
    return new VoidConditional(this::test, r, elseopt);
  }
  
  public VoidConditional elseIf(VoidConditional cnd) {
    Conditional<Void,Void> nc = super.elseIf((Conditional<Void,Void>)cnd);
    return new VoidConditional(()->nc.test(null), ()->nc.apply(null), Optional.of(nc));
  }
  
  public VoidConditional elseIf(Supplier<Boolean> s, Runnable r) {
    return elseIf(new VoidConditional(s, r));
  }
  
  public VoidConditional elseThen(Runnable r) {
    return elseIf(new VoidConditional(()->true, r));
  }
  
  public VoidConditional elseThrow(Supplier<Throwable> s) {
    return elseIf(new VoidConditional(()->true, ()->{throw Unchecked.unchecked(s.get());}));
  }
  
}
