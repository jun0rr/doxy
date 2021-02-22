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
public class DefaultOption implements Option {
  
  private final String name;
  
  private final Class type;
  
  private final String alias;
  
  private final String desc;
  
  private final boolean acceptArgs;
  
  private final boolean repeatable;
  
  private final BiConsumer<App,Optional> action;
  
  public DefaultOption(String name, Class type, String alias, String description, boolean acceptArgs, boolean isRepeatable, BiConsumer<App,Optional> action) {
    this.name = Objects.requireNonNull(name, "Bad null option name");
    this.type = type;
    this.alias = alias;
    this.desc = description;
    this.acceptArgs = acceptArgs;
    this.repeatable = isRepeatable;
    this.action = action;
  }
  
  @Override
  public String name() {
    return name;
  }
  
  @Override
  public Class type() {
    return type;
  }
  
  @Override
  public String alias() {
    return alias;
  }
  
  @Override
  public String description() {
    return desc;
  }
  
  @Override
  public BiConsumer<App,Optional> action() {
    return action;
  }
  
  @Override
  public boolean acceptArgument() {
    return acceptArgs;
  }
  
  @Override
  public boolean isRepeatable() {
    return repeatable;
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
        || this.name.equals(other.alias())
        || (this.alias != null 
          && (this.alias.equals(other.alias()) 
            || this.alias.equals(other.name())));
  }
  
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder(name());
    if(alias() != null && !alias().isBlank()) {
      str.append("/").append(alias());
    }
    return str.toString();
  }
  
  public String toLongString() {
    return "Option{\n" 
        + "  - name=" + name + ",\n"
        + "  - type=" + type + ",\n"
        + "  - alias=" + alias + ",\n"
        + "  - description=" + desc + ",\n"
        + "  - acceptArgs=" + acceptArgs + ",\n"
        + "  - isRepeatable=" + repeatable + ",\n}";
  }
  
}
