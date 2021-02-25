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
 * This rule block options to appear together in options list.
 * If NOT mandatory, this rule will be applied only if at least one 
 * option is present, otherwise will be allways applied. 
 * @author Juno - juno.rr@gmail.com
 */
public class XorOptionRule implements OptionRule {
  
  private final Collection<Option> options;
  
  private final boolean mandatory;
  
  /**
   * Create a XorOptionRule. All options informed must NOT be present together in option list.
   * @param mandatory Setting to true means this rule will be allways applied, demanding 
   * at least one of the options must allways be present and the others not.
   * @param options Options that must NOT be present together
   */
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
