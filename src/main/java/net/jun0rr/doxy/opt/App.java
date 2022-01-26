/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.opt;

import java.util.Collection;
import net.jun0rr.doxy.opt.rule.OptionRule;


/**
 *
 * @author Juno
 */
public interface App {
  
  public String name();
  
  public String author();
  
  public String displayAuthor();
  
  public String contact();
  
  public String displayContact();
  
  public String license();
  
  public String displayLicense();
  
  public String version();
  
  public String displayVersion();
  
  public String displayHelp();
  
  public String description();
  
  public Collection<Option> options();
  
  public Collection<OptionRule> rules();
  
  public String header();
  
  public String header(int columns);
  
  public Collection<ParsedOption> parse(Collection<String> args) throws OptionParseException;
  
  
  public static AppBuilder of(String name) {
    return new AppBuilder(name);
  }
  
}
