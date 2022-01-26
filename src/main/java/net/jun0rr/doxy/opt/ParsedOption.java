/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.opt;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.jun0rr.doxy.common.StringValue;


/**
 *
 * @author Juno
 */
public class ParsedOption implements Option {
  
  private final Option opt;

  private final Optional<StringValue> value;

  public ParsedOption(Option o, Optional<StringValue> value) {
    this.opt = Objects.requireNonNull(o);
    this.value = Objects.requireNonNull(value);
  }

  public ParsedOption(Option o, String value) {
    this(o, Optional.of(new StringValue(Objects.requireNonNull(value))));
  }

  public ParsedOption(Option o) {
    this(o, Optional.empty());
  }

  public Optional<StringValue> stringValue() {
    return value;
  }
  
  public <T> Optional<T> value() {
    return (Optional<T>) value.map(s->s.getAs(type()));
  }
  
  public <T> Optional<T> eval(App app) {
    Optional<T> val = (Optional<T>) value.map(s->s.getAs(type()));
    opt.action().accept(app, val);
    return val;
  }
  
  @Override
  public String name() {
    return opt.name();
  }
  
  @Override
  public Class type() {
    return opt.type();
  }
  
  @Override
  public String alias() {
    return opt.alias();
  }
  
  @Override
  public BiConsumer<App,Optional> action() {
    return opt.action();
  }
  
  @Override
  public String description() {
    return opt.description();
  }

  @Override
  public boolean acceptArgument() {
    return opt.acceptArgument();
  }

  @Override
  public boolean isRepeatable() {
    return opt.isRepeatable();
  }

  @Override
  public boolean equals(Object o) {
    if(!opt.equals(o)) return false;
    if(ParsedOption.class.isAssignableFrom(o.getClass())) {
      return this.value.equals(((ParsedOption)o).value());
    }
    return true;
  }

  @Override
  public int hashCode() {
    return opt.hashCode() * value.hashCode();
  }


  @Override
  public String toString() {
    StringBuilder str = new StringBuilder(name());
    if(alias() != null && !alias().isBlank()) {
      str.append("/").append(alias());
    }
    value().ifPresent(v->str.append("(").append(v).append(")"));
    return str.toString();
  }

}
