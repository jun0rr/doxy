/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.opt;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import net.jun0rr.doxy.common.Conditional;
import net.jun0rr.doxy.opt.rule.OptionRule;


/**
 *
 * @author Juno
 */
public class DefaultApp implements App {
  
  public static final int DEFAULT_COLUMN_LENGTH = 40;
  
  private final String name;
  
  private final String author;
  
  private final String contact;
  
  private final String license;
  
  private final String version;
  
  private final String description;
  
  private final Collection<Option> options;
  
  private final Collection<OptionRule> rules;
  
  private final OptionParser parser;


  public DefaultApp(String name, String version, String author, String contact, String license, String description, Collection<Option> options, Collection<OptionRule> rules) {
    this.name = Objects.requireNonNull(name);
    this.version = version;
    this.author = author;
    this.contact = contact;
    this.license = license;
    this.description = description;
    this.options = options;
    this.rules = rules;
    /*Conditional<Collection,Collection> cond = 
        Conditional.<Collection>of(c->c != null && !c.isEmpty())
            .elseThrow(c->new IllegalArgumentException("Bad Collection: " + c));
    this.options = cond.apply(options).get();
    this.rules = cond.apply(rules).get();*/
    this.parser = new OptionParser(options, rules);
  }
  
  
  @Override
  public String name() {
    return name;
  }
  
  @Override
  public String author() {
    return author;
  }
  
  @Override
  public String displayAuthor() {
    if(author == null) return null;
    return String.format("Copyright (C) %d %s", LocalDate.now().getYear(), author);
  }
  
  @Override
  public String contact() {
    return contact;
  }
  
  @Override
  public String displayContact() {
    if(contact == null) return null;
    return String.format("<%s>", contact);
  }
  
  @Override
  public String license() {
    return license;
  }
  
  @Override
  public String displayLicense() {
    if(license == null) return null;
    return String.format("Licensed under %s", license);
  }
  
  @Override
  public String version() {
    return version;
  }
  
  @Override
  public String displayVersion() {
    if(version == null) return null;
    return String.format("%s - %s", name, version);
  }
  
  @Override
  public String description() {
    return description;
  }
  
  @Override
  public Collection<Option> options() {
    return options;
  }
  
  @Override
  public Collection<OptionRule> rules() {
    return rules;
  }
  
  @Override
  public String header() {
    return header(Math.max(DEFAULT_COLUMN_LENGTH, minColumnLength()));
  }
  
  public int minColumnLength() {
    Conditional<String,Integer> len = Conditional
        .of(s->s != null, String::length)
        .elseThen(s->0);
    return IntStream.of(
        len.apply(author).get() + 20, 
        len.apply(name).get() + len.apply(version).get() + 5,
        len.apply(contact).get() + 5,
        len.apply(license).get() + 20
    ).max().getAsInt();
  }
  
  @Override
  public String header(int columns) {
    if(columns < minColumnLength()) {
      throw new IllegalArgumentException(String.format(
          "Insuficient columns to display header (%d < %d)", columns, minColumnLength())
      );
    }
    int cols = columns -2;
    StringBuilder str = new StringBuilder();
    str.append("+")
        .append(StringPad.of("").cpad("-", cols))
        .append("+\n")
        .append("|");
    if(version != null && !version.isBlank()) {
      str.append(StringPad.of(displayVersion()).cpad(" ", cols));
    }
    else {
      str.append(StringPad.of(name).cpad(" ", cols));
    }
    str.append("|\n");
    str.append("|")
        .append(StringPad.of("").cpad("-", cols))
        .append("|\n");
    if(description != null) {
      List<String> lns = StringPad.of(description).centerLines(" ", cols);
      lns.forEach(l->str.append("|").append(l).append("|\n"));
    }
    if(author != null || contact != null || license != null) {
      str.append("|")
          .append(StringPad.of("*  *  *").cpad(" ", cols))
          .append("|\n");
    }
    if(author != null) {
      str.append("|")
          .append(StringPad.of(displayAuthor()).cpad(" ", cols))
          .append("|\n");
    }
    if(contact != null) {
      String fmt = "<%s>";
      str.append("|")
          .append(StringPad.of(displayContact()).cpad(" ", cols))
          .append("|\n");
    }
    if(license != null) {
      str.append("|")
          .append(StringPad.of(displayLicense()).cpad(" ", cols))
          .append("|\n");
    }
    str.append("+")
        .append(StringPad.of("").cpad("-", cols))
        .append("+");
    return str.toString();
  }
  
  @Override
  public String displayHelp() {
    /*
    +--------------------------------------+
    |                MyApp                 |
    |--------------------------------------|
    |     Lorem ipsum dolor sit amet,      |
    |     consectetur adipiscing elit.     |
    |     Curabitur sit amet risus mi.     |
    |               *  *  *                |
    |   Copyright (C) 2020 Juno Roesler    |
    +--------------------------------------+
     Usage: MyApp [options]
     Option               Argument  Description
      -c/--client         <host>    Client host
      -s/--server         <host>    Server host
      -r/--remote         <host>    Remote host
      -p/--proxy          <host>    Proxy host
      --proxy-user        <string>  Proxy user
      --proxy-pass        <string>  Proxy password
      -k/--keystore       <path>    Keystore path (https)
      --keystore-pass     <string>  Keystore password (https)
      --private-key       <path>    Private key path
      --public-key        <path>    Public key path
      --crypt-algo        <string>  Cryptographic algorithm
      -n/--server-name    <string>  Server http header
      -u/--user-agent     <string>  User-Agent http header
      -b/--buffer         <int>     Buffer size
      -d/--direct-buffer   -        Use direct buffers
      -t/--thread-pool    <int>     Thread pool size
      --timeout           <long>    Server timeout in millis
      --version            -        Print MyApp Version
      --author             -        Print MyApp Author
      --license            -        Print MyApp License
      -h/--help            -        Print MyApp Usage
    */
    StringBuilder str = new StringBuilder(header()).append("\n");
    if(options.isEmpty()) {
      str.append(String.format(" Usage: %s\n", name()));
      return str.toString();
    }
    str.append(String.format(" Usage: %s [options]\n", name()));
    int maxoptlen = options.stream()
        .map(Objects::toString)
        .mapToInt(String::length)
        .max().getAsInt() + 4;
    int maxtypelen = options.stream()
        .filter(o->o.type() != null)
        .map(Option::type)
        .map(Class::getSimpleName)
        .mapToInt(String::length)
        .max().getAsInt() + 4;
    str.append(StringPad.of(" Option").rpad(" ", maxoptlen))
        .append(StringPad.of("Argument").rpad(" ", Math.max(maxtypelen, 10)))
        .append("Description\n");
    for(Option o : options) {
      str.append(StringPad.of(String.format("  %s", o)).rpad(" ", maxoptlen));
      String type = !o.acceptArgument() || o.type() == null ? " -" 
          : String.format("<%s>", o.type().getSimpleName());
      str.append(StringPad.of(type).rpad(" ", maxtypelen));
      if(o.description() != null) {
        str.append(o.description());
      }
      str.append("\n");
    }
    return str.toString();
  }
  
  @Override
  public Collection<ParsedOption> parse(Collection<String> args) throws OptionParseException {
    Collection<ParsedOption> opts = parser.parse(args);
    opts.stream()
        .filter(o->o.action() != null)
        .forEach(o->o.action().accept(this, o.value()));
    return opts;
  }
  
}
