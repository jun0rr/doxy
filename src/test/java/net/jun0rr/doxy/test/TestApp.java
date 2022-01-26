/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.opt.App;
import net.jun0rr.doxy.opt.Option;
import net.jun0rr.doxy.opt.ParsedOption;
import net.jun0rr.doxy.opt.rule.AndOptionRule;
import net.jun0rr.doxy.opt.rule.DependencyOptionRule;
import net.jun0rr.doxy.opt.rule.IncompatibilityOptionRule;
import net.jun0rr.doxy.opt.rule.XorOptionRule;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestApp {
  
  @Test
  public void app() {
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
    Option client = Option.of("-c")
        .alias("--client")
        .acceptArgument(true)
        .type(Host.class)
        .<Host>action((a,v)->System.out.println(v.get().getHostname()))
        .description("Client host")
        .get();
    Option server = Option.of("-s")
        .alias("--server")
        .acceptArgument(true)
        .type(Host.class)
        .description("Server host")
        .get();
    Option remote = Option.of("-r")
        .alias("--remote")
        .acceptArgument(true)
        .type(Host.class)
        .description("Remote host")
        .get();
    Option target = Option.of("-t")
        .alias("--target")
        .acceptArgument(true)
        .type(Host.class)
        .description("Target host")
        .get();
    Option proxy = Option.of("-p")
        .alias("--proxy")
        .acceptArgument(true)
        .type(Host.class)
        .description("Proxy host")
        .get();
    Option proxyUser = Option.of("-pu")
        .alias("--proxy-user")
        .acceptArgument(true)
        .type(String.class)
        .description("Proxy user")
        .get();
    Option proxyPass = Option.of("-pp")
        .alias("--proxy-password")
        .acceptArgument(true)
        .type(String.class)
        .description("Proxy password")
        .get();
    Option keystore = Option.of("-k")
        .alias("--keystore")
        .acceptArgument(true)
        .type(Path.class)
        .description("Keystore file")
        .get();
    Option keystorePass = Option.of("-kp")
        .alias("--keystore-pass")
        .acceptArgument(true)
        .type(String.class)
        .description("Keystore password")
        .get();
    Option pk = Option.of("-pk")
        .alias("--private-key")
        .acceptArgument(true)
        .type(Path.class)
        .description("Private key file")
        .<Path>action((a,v)->System.out.println(v.get().normalize()))
        .get();
    Option pub = Option.of("-pub")
        .alias("--public-key")
        .acceptArgument(true)
        .type(Path.class)
        .description("Public key file")
        .get();
    Option algo = Option.of("-ca")
        .alias("--crypt-algo")
        .acceptArgument(true)
        .type(String.class)
        .description("Cryptography algorithm")
        .get();
    Option serverName = Option.of("-n")
        .alias("--server-name")
        .acceptArgument(true)
        .type(String.class)
        .description("Server (Http Header)")
        .get();
    Option userAgent = Option.of("-u")
        .alias("--user-agent")
        .acceptArgument(true)
        .type(String.class)
        .description("User-Agent (Http Header)")
        .get();
    Option buffer = Option.of("-b")
        .alias("--buffer")
        .acceptArgument(true)
        .type(int.class)
        .description("Buffer size in bytes")
        .get();
    Option directBuffer = Option.of("-d")
        .alias("--direct-buffer")
        .acceptArgument(false)
        .description("Use direct buffers")
        .get();
    Option threads = Option.of("-tp")
        .alias("--thread-pool")
        .acceptArgument(true)
        .type(int.class)
        .description("Thread pool size")
        .get();
    Option timeout = Option.of("-tm")
        .alias("--timeout")
        .acceptArgument(true)
        .type(long.class)
        .description("Server timeout in millis")
        .get();
    Option quiet = Option.of("-q")
        .alias("--quiet")
        .acceptArgument(false)
        .description("Dont print stuff")
        .get();
    try {
    App app = App.of("MyApp")
        .version("0.1")
        .author("Juno Roesler")
        .contact("juno.rr@gmail.com")
        .license("Apache License 2.0")
        .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet risus mi.")
        .setHelpOptionEnabled(true)
        .addOption(client)
        .addOption(server)
        .addOption(remote)
        .addOption(target)
        .addOption(proxy)
        .addOption(proxyUser)
        .addOption(proxyPass)
        .addOption(keystore)
        .addOption(keystorePass)
        .addOption(pk)
        .addOption(pub)
        .addOption(algo)
        .addOption(serverName)
        .addOption(userAgent)
        .addOption(buffer)
        .addOption(directBuffer)
        .addOption(threads)
        .addOption(quiet)
        .addRule(new IncompatibilityOptionRule(client, server, keystore, keystorePass, serverName))
        .addRule(new IncompatibilityOptionRule(server, client, userAgent))
        .addRule(new AndOptionRule( false, client, remote, target, pk))
        .addRule(new AndOptionRule( false, server, pub))
        //.addRule(new DependencyOptionRule(remote, client))
        //.addRule(new DependencyOptionRule(server, pub))
        .addRule(new DependencyOptionRule(proxy, client))
        .addRule(new DependencyOptionRule(proxyUser, proxy))
        .addRule(new DependencyOptionRule(proxyPass, proxy, proxyUser))
        .addRule(new DependencyOptionRule(keystore, server))
        .addRule(new DependencyOptionRule(keystorePass, keystore))
        //.addRule(new DependencyOptionRule(pk, client))
        //.addRule(new DependencyOptionRule(pub, server))
        .addRule(new DependencyOptionRule(userAgent, client))
        .addRule(new DependencyOptionRule(serverName, server))
        .addRule(new DependencyOptionRule(timeout, server))
        .addRule(new XorOptionRule(true, client, server))
        .get();
    System.out.println(app.displayHelp());
    //String args = "-c localhost:13001 -s 0.0.0.0:13000 -r localhost:6060";
    //String args = "-c localhost:13001 -r localhost:6060 -p localhost:40080 -pk d:/java/doxy-pk.pem";
    String args = "-c localhost:13001 -r leitao.hopto.org:80 -t localhost:6060 -p localhost:40080 -pk d:/java/doxy-pk.pem";
    //String args = "-s 0.0.0.0:13000 -r localhost:6060";
    //String args = "-c localhost:13001 -r localhost:6060 -p localhost:40080 -pp 32132155 -pk 'd:/java/doxy-pk.pem'";
    //String args = "--author";
    //try {
      Collection<ParsedOption> opts = app.parse(List.of(args.split(" ")));
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
