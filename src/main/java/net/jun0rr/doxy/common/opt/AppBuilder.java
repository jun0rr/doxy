/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common.opt;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import net.jun0rr.doxy.common.opt.rule.OptionRule;
import net.jun0rr.doxy.common.opt.rule.SupressErrorOptionRule;
import net.jun0rr.doxy.common.opt.rule.XorOptionRule;


/**
 *
 * @author Juno
 */
public class AppBuilder {
  
  private final String name;
  
  private final String author;
  
  private final String contact;
  
  private final String license;
  
  private final String version;
  
  private final String description;
  
  private final boolean autoVersion;
  
  private final boolean autoAuthor;
  
  private final boolean autoLicense;
  
  private final boolean autoHelp;
  
  private final Collection<Option> options;
  
  private final Collection<OptionRule> rules;


  public AppBuilder(String name, String version, String author, String contact, String license, String description, boolean autoVersion, boolean autoAuthor, boolean autoLicense, boolean autoHelp, Collection<Option> options, Collection<OptionRule> rules) {
    this.name = Objects.requireNonNull(name);
    this.version = version;
    this.author = author;
    this.contact = contact;
    this.license = license;
    this.description = description;
    this.autoVersion = autoVersion;
    this.autoAuthor = autoAuthor;
    this.autoLicense = autoLicense;
    this.autoHelp = autoHelp;
    this.options = options;
    this.rules = rules;
  }
  
  public AppBuilder(String name) {
    this(Objects.requireNonNull(name), null, null, null, null, null, false, false, false, false, new LinkedList<>(), new LinkedList<>());
  }
  
  public AppBuilder(String name, String version) {
    this(Objects.requireNonNull(name), version, null, null, null, null, false, false, false, false, new LinkedList<>(), new LinkedList<>());
  }
  
  public String name() {
    return name;
  }
  
  public AppBuilder name(String name) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, autoAuthor, autoLicense, autoHelp, options, rules);
  }
  
  public String author() {
    return author;
  }
  
  public AppBuilder author(String author) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, true, autoLicense, autoHelp, options, rules);
  }
  
  public String contact() {
    return contact;
  }
  
  public AppBuilder contact(String contact) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, autoAuthor, autoLicense, autoHelp, options, rules);
  }
  
  public String license() {
    return license;
  }
  
  public AppBuilder license(String license) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, autoAuthor, true, autoHelp, options, rules);
  }
  
  public String version() {
    return version;
  }
  
  public AppBuilder version(String version) {
    return new AppBuilder(name, version, author, contact, license, description, true, autoAuthor, autoLicense, autoHelp, options, rules);
  }
  
  public String description() {
    return description;
  }
  
  public AppBuilder description(String description) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, autoAuthor, autoLicense, autoHelp, options, rules);
  }
  
  public boolean isVersionOptionEnabled() {
    return autoVersion;
  }
  
  public AppBuilder setVersionOptionEnabled(boolean enable) {
    return new AppBuilder(name, version, author, contact, license, description, enable, autoAuthor, autoLicense, autoHelp, options, rules);
  }
  
  public boolean isAuthorOptionEnabled() {
    return autoAuthor;
  }
  
  public AppBuilder setAuthorOptionEnabled(boolean enable) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, enable, autoLicense, autoHelp, options, rules);
  }
  
  public boolean isLicenseOptionEnabled() {
    return autoLicense;
  }
  
  public AppBuilder setLicenseOptionEnabled(boolean enable) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, autoAuthor, enable, autoHelp, options, rules);
  }
  
  public boolean isHelpOptionEnabled() {
    return autoHelp;
  }
  
  public AppBuilder setHelpOptionEnabled(boolean enable) {
    return new AppBuilder(name, version, author, contact, license, description, autoVersion, autoAuthor, autoLicense, enable, options, rules);
  }
  
  public Collection<Option> options() {
    return options;
  }
  
  public Collection<OptionRule> rules() {
    return rules;
  }
  
  public AppBuilder addOption(Option opt) {
    if(opt != null) {
      options.add(opt);
    }
    return this;
  }
  
  public AppBuilder addRule(OptionRule rule) {
    if(rule != null) {
      rules.add(rule);
    }
    return this;
  }
  
  private void setupAutoOptions() {
    Option versionOpt = Option.of("--version")
        .description("Show " + name + " version")
        .action((a,v)->System.out.println(a.displayVersion()))
        .get();
    Option authorOpt = Option.of("--author")
        .description("Show " + name + " author")
        .action((a,v)->System.out.printf("%s - %s%n", a.name(), a.displayAuthor()))
        .get();
    Option licenseOpt = Option.of("--license")
        .description("Show " + name + " license")
        .action((a,v)->System.out.printf("%s - %s%n", a.name(), a.displayLicense()))
        .get();
    Option helpOpt = Option.of("-h")
        .alias("--help")
        .description("Show " + name + " help")
        .action((a,v)->System.out.println(a.displayHelp()))
        .get();
    Collection<Option> autoOpts = new LinkedList<>();
    if(autoVersion) {
      options.add(versionOpt);
      autoOpts.add(versionOpt);
    }
    if(autoAuthor) {
      options.add(authorOpt);
      autoOpts.add(authorOpt);
    }
    if(autoLicense) {
      options.add(licenseOpt);
      autoOpts.add(licenseOpt);
    }
    if(autoHelp) {
      options.add(helpOpt);
      autoOpts.add(helpOpt);
    }
    if(!autoOpts.isEmpty()) {
      Collection<OptionRule> supressed = rules.stream()
          .map(r->new SupressErrorOptionRule(r, autoOpts))
          .collect(Collectors.toList());
      rules.clear();
      rules.addAll(supressed);
      rules.add(new XorOptionRule(false, autoOpts));
    }
  }
  
  public App get() {
    setupAutoOptions();
    return new DefaultApp(name, version, author, contact, license, description, options, rules);
  }
  
}
