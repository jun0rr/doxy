/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.opt.rule;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.jun0rr.doxy.opt.Option;
import net.jun0rr.doxy.opt.OptionParseException;
import net.jun0rr.doxy.opt.ParsedOption;


/**
 * Supress a rule evaluation error if one of the options in arguments is present.
 * @author Juno - juno.rr@gmail.com
 */
public class SupressErrorOptionRule implements OptionRule {
  
  private final OptionRule rule;
  
  private final Collection<Option> options;
  
  public SupressErrorOptionRule(OptionRule rule, Collection<Option> options) {
    this.rule = Objects.requireNonNull(rule);
    this.options = Objects.requireNonNull(options);
  }
  
  public SupressErrorOptionRule(OptionRule rule, Option... options) {
    this(rule, List.of(options));
  }
  
  @Override
  public void eval(Collection<ParsedOption> opts) throws OptionParseException {
    try {
      rule.eval(opts);
    }
    catch(OptionParseException e) {
      //System.out.println("## Error Supressed: " + e.toString());
      if(opts.stream().noneMatch(o->options.stream().anyMatch(o::equals))) {
        throw e;
      }
    }
  }
  
}
