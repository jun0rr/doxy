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
public class XorOptionRule implements OptionRule {
  
  private final Collection<Option> options;
  
  private final boolean mandatory;
  
  public XorOptionRule(boolean mandatory, Option... options) {
    this(mandatory, List.of(Objects.requireNonNull(options)));
  }
  
  public XorOptionRule(boolean mandatory, Collection<Option> options) {
    this.mandatory = mandatory;
    this.options = Objects.requireNonNull(options);
  }
  
  @Override
  public void eval(Collection<ParsedOption> opts) throws OptionParseException {
    Optional<Option> found = options.stream().filter(o->opts.stream().anyMatch(o::equals)).findFirst();
    if(found.isPresent()) {
      Optional<Option> other = options.stream()
          .filter(o->!found.get().equals(o))
          .filter(o->opts.stream().anyMatch(o::equals))
          .findFirst();
      if(other.isPresent()) {
        throw new OptionParseException(String.format("Illegal option combination: [%s, %s]", found.get(), other.get()));
      }
    }
    else if(mandatory) {
      throw new OptionParseException("Option missing: %s", options.toString()
          .replaceAll("\\[", "<")
          .replaceAll("\\]", ">")
          .replaceAll(", ", " | ")
      );
    }
  }
  
}
