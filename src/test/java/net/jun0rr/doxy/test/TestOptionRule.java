/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.util.List;
import net.jun0rr.doxy.common.opt.Option;
import net.jun0rr.doxy.common.opt.OptionParseException;
import net.jun0rr.doxy.common.opt.ParsedOption;
import net.jun0rr.doxy.common.opt.StringPad;
import net.jun0rr.doxy.common.opt.rule.AndOptionRule;
import net.jun0rr.doxy.common.opt.rule.XorOptionRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestOptionRule {
  
  @Test
  public void exclusive_option_ok() {
    Option help = Option.of("-h")
        .alias("--help")
        .description("Show this help text")
        .acceptArgument(false)
        .setRepeatable(false)
        .get();
    List<ParsedOption> opts = List.of(
        new ParsedOption(Option.of("-h").get())
    );
    XorOptionRule rule = new XorOptionRule(true, help);
    rule.eval(opts);
  }
  
  @Test
  public void mandatory_option_ok() {
    Option help = Option.of("-h")
        .alias("--help")
        .description("Show this help text")
        .acceptArgument(false)
        .setRepeatable(false)
        .get();
    List<ParsedOption> opts = List.of(
        new ParsedOption(Option.of("-h").get()),
        new ParsedOption(Option.of("-p").get(), "8080")
    );
    AndOptionRule rule = new AndOptionRule(true, help);
    rule.eval(opts);
  }
  
  @Test
  public void mandatory_option_error() {
    Option help = Option.of("-h")
        .alias("--help")
        .description("Show this help text")
        .acceptArgument(false)
        .setRepeatable(false)
        .get();
    List<ParsedOption> opts = List.of(
        new ParsedOption(Option.of("-a").get()),
        new ParsedOption(Option.of("-p").get(), "8080")
    );
    AndOptionRule rule = new AndOptionRule(true, help);
    OptionParseException ex = Assertions.assertThrows(OptionParseException.class, ()->rule.eval(opts));
    System.out.println("--- mandatory_option_error() throws: " + ex);
  }
  
  @Test
  public void xor_option_ok() {
    Option a = Option.of("-a")
        .alias("--a-char")
        .description("Set a char")
        .acceptArgument(true)
        .setRepeatable(false)
        .get();
    Option b = Option.of("-b")
        .alias("--b-option")
        .description("Enable b option")
        .acceptArgument(false)
        .setRepeatable(false)
        .get();
    List<ParsedOption> opts = List.of(
        new ParsedOption(Option.of("-a").get(), "a"),
        new ParsedOption(Option.of("-c").get(), "0")
    );
    XorOptionRule rule = new XorOptionRule(true, a, b);
    rule.eval(opts);
  }
  
  @Test
  public void xor_option_error() {
    Option a = Option.of("-a")
        .alias("--a-char")
        .description("Set a char")
        .acceptArgument(true)
        .setRepeatable(false)
        .get();
    Option b = Option.of("-b")
        .alias("--b-option")
        .description("Enable b option")
        .acceptArgument(false)
        .setRepeatable(false)
        .get();
    List<ParsedOption> opts = List.of(
        new ParsedOption(Option.of("-a").get(), "a"),
        new ParsedOption(Option.of("-b").get()),
        new ParsedOption(Option.of("-c").get())
    );
    XorOptionRule rule = new XorOptionRule(true, a, b);
    OptionParseException ex = Assertions.assertThrows(OptionParseException.class, ()->rule.eval(opts));
    System.out.println("--- xor_option_error() throws: " + ex);
  }
  
  @Test
  public void xor_option_error_missing() {
    Option a = Option.of("-a")
        .alias("--a-char")
        .description("Set a char")
        .acceptArgument(true)
        .setRepeatable(false)
        .get();
    Option b = Option.of("-b")
        .alias("--b-option")
        .description("Enable b option")
        .acceptArgument(false)
        .setRepeatable(false)
        .get();
    List<ParsedOption> opts = List.of(
        new ParsedOption(Option.of("-c").get(), "a"),
        new ParsedOption(Option.of("-d").get()),
        new ParsedOption(Option.of("-e").get())
    );
    XorOptionRule rule = new XorOptionRule(true, a, b);
    OptionParseException ex = Assertions.assertThrows(OptionParseException.class, ()->rule.eval(opts));
    System.out.println("--- xor_option_error_missing() throws: " + ex);
  }
  
  @Test
  public void string_pad() {
    System.out.print("--- string_pad(): ");
    System.out.println(StringPad.of("hello").concat("-", 30, "A", "B"));
    System.out.println(StringPad.of("A").concat(" ", 30, "B", "C", "D"));
    System.out.println(StringPad.of("hello").cpad("-", 30));
    System.out.println(StringPad.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet risus mi. Nulla dui purus, auctor eget urna quis, fermentum viverra nibh. Donec ultricies aliquam sollicitudin. Nulla et laoreet.")
        .justify(" ", 30));
    List<String> lns = StringPad.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet risus mi. Nulla dui purus, auctor eget urna quis, fermentum viverra nibh. Donec ultricies aliquam sollicitudin. Nulla et laoreet.")
        .centerLines(" ", 30);
    lns.forEach(System.out::println);
  }
  
}
