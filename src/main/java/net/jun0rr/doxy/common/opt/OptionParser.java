/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common.opt;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.common.opt.rule.OptionRule;


/**
 *
 * @author Juno
 */
public class OptionParser {
  
  private final Collection<Option> options;
  
  private final Collection<OptionRule> rules;
  
  
  public OptionParser(Collection<Option> options, Collection<OptionRule> rules) {
    this.options = Objects.requireNonNull(options);
    this.rules = Objects.requireNonNull(rules);
  }
  
  public List<ParsedOption> parse(Collection<String> args) throws OptionParseException {
    List<ParsedOption> parsed = new LinkedList<>();
    Optional<ParsedOption> current = Optional.empty();
    int argidx = 0;
    //-a 1 -b 2 -c -d 3
    for(String a : args) {
      Optional<Option> opt = options.stream()
          .filter(o->o.name().equals(a) || (o.alias() != null && o.alias().equals(a)))
          .findAny();
      if(opt.isPresent()) {
        current.ifPresent(parsed::add);
        current = Optional.of(new ParsedOption(opt.get()));
        if(parsed.stream().anyMatch(o->opt.get().equals(o)) && !opt.get().isRepeatable()) {
          throw new OptionParseException("Duplicated option not allowed: %s", opt.get());
        }
      }
      else if(current.isPresent() && current.get().acceptArgument()) {
        current = Optional.of(new ParsedOption(current.get(), a));
      }
      else {
        Option arg = Option.of("--app-argument")
            .alias(String.valueOf(argidx++))
            .acceptArgument(true)
            .setRepeatable(true)
            .get();
        parsed.add(new ParsedOption(arg, a));
        //throw new OptionParseException("Unexpected option argument: %s <%s>", current.get(), a);
      }
    }
    current.ifPresent(parsed::add);
    rules.stream()
        //.peek(System.out::println)
        .forEach(r->r.eval(parsed));
    return parsed;
  }
  
}
