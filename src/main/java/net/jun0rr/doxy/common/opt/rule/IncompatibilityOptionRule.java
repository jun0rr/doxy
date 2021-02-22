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
public class IncompatibilityOptionRule implements OptionRule {
  
  private final Option opt;
  
  private final Collection<Option> incompatibles;
  
  public IncompatibilityOptionRule(Option opt, Option... incompatibles) {
    this(opt, List.of(Objects.requireNonNull(incompatibles)));
  }
  
  public IncompatibilityOptionRule(Option opt, Collection<Option> incompatibles) {
    this.opt = Objects.requireNonNull(opt);
    this.incompatibles = Objects.requireNonNull(incompatibles);
  }
  
  @Override
  public void eval(Collection<ParsedOption> opts) throws OptionParseException {
    if(opts.stream().anyMatch(opt::equals) 
        && incompatibles.stream().anyMatch(o->opts.stream().anyMatch(o::equals))) {
      throw new OptionParseException(String.format("Illegal option combination: [%s, %s]", opt, incompatibles));
    }
  }
  
}
