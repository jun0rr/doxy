/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class ConsumerConditional<T> extends Conditional<T,Void> implements Consumer<T> {
  
  protected ConsumerConditional(Predicate<T> test, Consumer<T> cs, Optional<Conditional<T,Void>> els) {
    super(test, x->{cs.accept(x); return null;}, els);
  }
  
  public ConsumerConditional(Predicate<T> test, Consumer<T> cs) {
    this(test, cs, Optional.empty());
  }
  
  public static <X> ConsumerConditional<X> of(Predicate<X> test, Consumer<X> cs) {
    return new ConsumerConditional(test, cs);
  }
  
  public static <X> ConsumerConditional<X> eval(Predicate<X> test) {
    return new ConsumerConditional(test, x->{});
  }
  
  @Override
  public void accept(T t) {
    this.apply(t);
  }
  
  public <X> ConsumerConditional<X> consume(Consumer<X> cs) {
    return new ConsumerConditional(test, cs, elseopt);
  }
  
  public ConsumerConditional<T> elseIf(ConsumerConditional<T> cnd) {
    Conditional<T,Void> nc = super.elseIf((Conditional<T,Void>)cnd);
    return new ConsumerConditional<>(nc::test, nc::apply, Optional.of(nc));
  }
  
  public ConsumerConditional elseIf(Predicate<T> test, Consumer<T> cs) {
    return elseIf(new ConsumerConditional(test, cs));
  }
  
  public ConsumerConditional elseThen(Consumer<T> cs) {
    return elseIf(new ConsumerConditional(x->true, cs));
  }
  
  @Override
  public ConsumerConditional<T> elseThrow(Function<T,Throwable> s) {
    return elseIf(new ConsumerConditional<>(i->true, i->{throw Unchecked.unchecked(s.apply(i));}));
  }
  
}
