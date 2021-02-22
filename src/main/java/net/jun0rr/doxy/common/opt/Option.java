/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common.opt;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;


/**
 *
 * @author Juno
 */
public interface Option {
  
  public Class type();
  
  public String name();
  
  public String alias();
  
  public String description();
  
  public boolean acceptArgument();
  
  public boolean isRepeatable();
  
  public BiConsumer<App,Optional> action(); 
  
  
  public static OptionBuilder of(String name) {
    return new OptionBuilder(name);
  }
  
  public static Predicate<Option> filter(String name) {
    return new OptionBuilder(name).get()::equals;
  }
  
}
