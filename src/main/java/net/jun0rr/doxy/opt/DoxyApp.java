/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.opt;

import java.util.Collection;
import java.util.Objects;
import net.jun0rr.doxy.cfg.DoxyConfigBuilder;
import net.jun0rr.doxy.opt.rule.OptionRule;


/**
 *
 * @author Juno
 */
public class DoxyApp implements App {
  
  private final App app;
  
  private final DoxyConfigBuilder bld;
  
  //private final Collection<ParsedOption> parsedOpts;
  
  public DoxyApp(App app) {
    this.app = Objects.requireNonNull(app);
    this.bld = DoxyConfigBuilder.newBuilder();
    //this.parsedOpts = Objects.requireNonNull(parsedOpts);
  }
  
  public DoxyConfigBuilder getDoxyConfigBuilder() {
    return bld;
  }
  
  @Override
  public String name() {
    return app.name();
  }
  
  @Override
  public String author() {
    return app.author();
  }
  
  @Override
  public String displayAuthor() {
    return app.displayAuthor();
  }
  
  @Override
  public String contact() {
    return app.contact();
  }
  
  @Override
  public String displayContact() {
    return app.displayContact();
  }
  
  @Override
  public String license() {
    return app.license();
  }
  
  @Override
  public String displayLicense() {
    return app.displayLicense();
  }
  
  @Override
  public String version() {
    return app.version();
  }
  
  @Override
  public String displayVersion() {
    return app.displayVersion();
  }
  
  @Override
  public String displayHelp() {
    return app.displayHelp();
  }
  
  @Override
  public String description() {
    return app.description();
  }
  
  @Override
  public Collection<Option> options() {
    return app.options();
  }
  
  @Override
  public Collection<OptionRule> rules() {
    return app.rules();
  }
  
  @Override
  public String header() {
    return app.header();
  }
  
  @Override
  public String header(int columns) {
    return app.header(columns);
  }
  
  @Override
  public Collection<ParsedOption> parse(Collection<String> args) throws OptionParseException {
    return app.parse(args);
  }
  
}
