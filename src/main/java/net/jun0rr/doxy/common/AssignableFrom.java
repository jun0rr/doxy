/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 *
 * @author Juno
 */
public class AssignableFrom<X> extends Conditional<Object,X> {
  
  protected AssignableFrom(Predicate<Object> p, Function<Object,X> f, Optional<Conditional<Object,X>> o) {
    super(p, f, o);
  }
  
  protected AssignableFrom(Class cls, Function<Object,X> f, Optional<Conditional<Object,X>> o) {
    this(x->Optional.ofNullable(x).map(e->cls.isAssignableFrom(e.getClass())).orElse(Boolean.FALSE), f, o);
  }
  
  public AssignableFrom(Class<X> c, Function<Object,X> f) {
    this(c, f, Optional.empty());
  }
  
  public static <Y,Z> AssignableFrom<Z> of(Class<Y> c, Function<Y,Z> f) {
    return new AssignableFrom(c, f);
  }
  
  public <Z> AssignableFrom<X> elseOf(Class<Z> c, Function<Z,X> f) {
    AssignableFrom cnd = new AssignableFrom(c, f);
    return new AssignableFrom(this::test, this::apply, elseopt
        .map(n->n.elseIf(cnd))
        .or(()->Optional.of(cnd))
    );
  }
  
  public <Y,Z> AssignableFrom<Z> map(Class<Y> c, Function<Y,Z> f) {
    return new AssignableFrom(c, f, elseopt);
  }
  
}
