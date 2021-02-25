/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common.opt.rule;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.common.opt.Option;
import net.jun0rr.doxy.common.opt.OptionParseException;
import net.jun0rr.doxy.common.opt.ParsedOption;


/**
 * Group options that must to be present together in option list.
 * If NOT mandatory, this rule will be applied only if at least one 
 * option is present, otherwise will be allways applied. 
 * @author Juno - juno.rr@gmail.com
 */
public class AndOptionRule implements OptionRule {
  
  private final Collection<Option> options;
  
  private final boolean mandatory;
  
  /**
   * Create an AndOptionRule. All options informed must be present together in option list.
   * @param mandatory Setting to true means this rule will be allways applied, demanding 
   * all options must allways be present.
   * @param options Options that must to be present together
   */
  public AndOptionRule(boolean mandatory, Option... options) {
    this(mandatory, List.of(Objects.requireNonNull(options)));
  }
  
  public AndOptionRule(boolean mandatory, Collection<Option> options) {
    this.mandatory = mandatory;
    this.options = Objects.requireNonNull(options);
  }
  
  @Override
  public void eval(Collection<ParsedOption> opts) throws OptionParseException {
    Optional<Option> opt = options.stream()
        .filter(o->opts.stream().anyMatch(o::equals))
        .findAny();
    boolean allMatch = options.stream().allMatch(o->opts.stream().anyMatch(o::equals));
    if(!allMatch && (mandatory || opt.isPresent())) {
      Option missing = options.stream()
          .filter(o->opts.stream().noneMatch(o::equals))
          .findAny()
          .get();
      throw new OptionParseException("Mandatory option missing: %s", missing);
    }
  }
  
}
