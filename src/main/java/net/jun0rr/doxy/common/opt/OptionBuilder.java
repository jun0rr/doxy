/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common.opt;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 *
 * @author Juno
 */
public class OptionBuilder {
  
  private final String name;
  
  private final Class type;
  
  private final String alias;
  
  private final String desc;
  
  private final boolean acceptArg;
  
  private final boolean repeatable;
  
  private final BiConsumer<App,Optional> action;
  
  public OptionBuilder(String name) {
    this(name, null, null, null, false, false, null);
  }
  
  public OptionBuilder(String name, Class type, String alias, String description, boolean acceptArg, boolean isRepeatable, BiConsumer<App,Optional> action) {
    this.name = Objects.requireNonNull(name, "Bad null option name");
    this.type = type;
    this.alias = alias;
    this.desc = description;
    this.acceptArg = acceptArg;
    this.repeatable = isRepeatable;
    this.action = action;
  }
  
  public String name() {
    return name;
  }
  
  public OptionBuilder name(String name) {
    return new OptionBuilder(name, type, alias, desc, acceptArg, repeatable, action);
  }
  
  public Class type() {
    return type;
  }
  
  public OptionBuilder type(Class type) {
    return new OptionBuilder(name, type, alias, desc, acceptArg, repeatable, action);
  }
  
  public String alias() {
    return alias;
  }
  
  public OptionBuilder alias(String alias) {
    return new OptionBuilder(name, type, alias, desc, acceptArg, repeatable, action);
  }
  
  public BiConsumer<App,Optional> action() {
    return action;
  }
  
  public <T> OptionBuilder action(BiConsumer<App,Optional<T>> action) {
    return new OptionBuilder(name, type, alias, desc, acceptArg, repeatable, (BiConsumer)action);
  }
  
  public String description() {
    return desc;
  }
  
  public OptionBuilder description(String desc) {
    return new OptionBuilder(name, type, alias, desc, acceptArg, repeatable, action);
  }
  
  public boolean acceptArgument() {
    return acceptArg;
  }
  
  public OptionBuilder acceptArgument(boolean acceptArg) {
    return new OptionBuilder(name, type, alias, desc, acceptArg, repeatable, action);
  }
  
  public boolean isRepeatable() {
    return repeatable;
  }
  
  public OptionBuilder setRepeatable(boolean repeatable) {
    return new OptionBuilder(name, type, alias, desc, acceptArg, repeatable, action);
  }
  
  public Option get() {
    return new DefaultOption(name, type, alias, desc, acceptArg, repeatable, action);
  }
  
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + Objects.hashCode(this.name);
    hash = 53 * hash + Objects.hashCode(this.alias);
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
    if (!Option.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    final Option other = (Option) obj;
    return this.name.equals(other.name()) 
        || (this.alias != null && this.alias.equals(other.alias()));
  }
  
  @Override
  public String toString() {
    return "OptionBuilder{\n" 
        + "  - name=" + name + ",\n"
        + "  - alias=" + alias + ",\n"
        + "  - description=" + desc + ",\n"
        + "  - acceptArgs=" + acceptArg + ",\n"
        + "  - isRepeatable=" + repeatable + ",\n}";
  }
  
}
