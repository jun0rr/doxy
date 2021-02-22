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
 *
 * @author Juno
 */
public class AndOptionRule implements OptionRule {
  
  private final Collection<Option> options;
  
  private final boolean mandatory;
  
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
