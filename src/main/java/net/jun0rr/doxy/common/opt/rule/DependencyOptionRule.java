/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common.opt.rule;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.jun0rr.doxy.common.opt.Option;
import net.jun0rr.doxy.common.opt.OptionParseException;
import net.jun0rr.doxy.common.opt.ParsedOption;


/**
 *
 * @author Juno
 */
public class DependencyOptionRule implements OptionRule {
  
  private final Option opt;
  
  private final Collection<Option> dependencies;
  
  public DependencyOptionRule(Option opt, Option... dependencies) {
    this(opt, List.of(Objects.requireNonNull(dependencies)));
  }
  
  public DependencyOptionRule(Option opt, Collection<Option> dependencies) {
    this.opt = Objects.requireNonNull(opt);
    this.dependencies = Objects.requireNonNull(dependencies);
  }
  
  @Override
  public void eval(Collection<ParsedOption> opts) throws OptionParseException {
    if(opts.stream().anyMatch(opt::equals) 
        && !dependencies.stream().allMatch(o->opts.stream().anyMatch(o::equals))) {
      throw new OptionParseException(String.format("Option (%s) depends on: %s", opt, dependencies));
    }
  }
  
}
