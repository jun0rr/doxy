/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class Conditional<I,O> implements Function<I,Optional<O>>, Predicate<I> {
  
  protected final Predicate<I> test;
  
  protected final Function<I,O> action;
  
  protected final Optional<Conditional<I,O>> elseopt;
  
  protected Conditional(Predicate<I> test, Function<I,O> fn, Optional<Conditional<I,O>> els) {
    this.test = Objects.requireNonNull(test, "Bad null Predicate");
    this.action = Objects.requireNonNull(fn, "Bad null Function");
    this.elseopt = Objects.requireNonNull(els, "Bad null Optional else");
  }
  
  public Conditional(Predicate<I> p, Function<I,O> f, Conditional<I,O> els) {
    this(p, f, Optional.ofNullable(els));
  }
  
  public Conditional(Predicate<I> p, Function<I,O> f) {
    this(p, f, Optional.empty());
  }
  
  public static <X,Y> Conditional<X,Y> of(Predicate<X> p, Function<X,Y> f) {
    return new Conditional<>(p, f);
  }
  
  public static <X> Conditional<X,X> of(Predicate<X> p) {
    return of(p, Function.identity());
  }
  
  @Override
  public boolean test(I i) {
    return test.test(i);
  }
  
  @Override
  public Optional<O> apply(I i) {
    return test.test(i) 
        ? Optional.ofNullable(action.apply(i)) 
        : elseopt.flatMap(c->c.apply(i));
  }
  
  public <X> Conditional<I,X> map(Function<I,X> fn) {
    return new Conditional(test, fn, elseopt);
  }
  
  public Conditional<I,O> elseIf(Conditional<I,O> cnd) {
    return new Conditional(test, action, elseopt
        .map(c->c.elseIf(cnd))
        .or(()->Optional.of(cnd))
    );
  }
  
  public Conditional<I,O> elseIf(Predicate<I> p, Function<I,O> f) {
    return elseIf(new Conditional<>(p, f));
  }
  
  public Conditional<I,O> elseThen(Function<I,O> f) {
    return elseIf(new Conditional<>(i->true, f));
  }
  
  public Conditional<I,O> elseThrow(Function<I,Throwable> s) {
    return elseIf(new Conditional<>(i->true, i->{throw Unchecked.unchecked(s.apply(i));}));
  }
  
}
