/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.common.opt.Option;
import net.jun0rr.doxy.common.opt.rule.AndOptionRule;
import net.jun0rr.doxy.common.opt.rule.DependencyOptionRule;
import net.jun0rr.doxy.common.opt.rule.IncompatibilityOptionRule;
import net.jun0rr.doxy.common.opt.rule.OptionRule;
import net.jun0rr.doxy.common.opt.rule.XorOptionRule;

/**
 *
 * @author juno
 */
public interface DoxyAppOptions {
  
  public static final Option CLIENT = Option.of("-c")
        .alias("--client")
        .acceptArgument(true)
        .type(Host.class)
        .<Host>action((a,v)->System.out.println(v.get().getHostname()))
        .description("Client host")
        .get();
  
  public static final Option SERVER = Option.of("-s")
        .alias("--server")
        .acceptArgument(true)
        .type(Host.class)
        .description("Server host")
        .get();
  
  public static final Option REMOTE = Option.of("-r")
        .alias("--remote")
        .acceptArgument(true)
        .type(Host.class)
        .description("Remote host")
        .get();
  
  public static final Option PROXY = Option.of("-p")
        .alias("--proxy")
        .acceptArgument(true)
        .type(Host.class)
        .description("Proxy host")
        .get();
  
  public static final Option PROXY_USER = Option.of("-pu")
        .alias("--proxy-user")
        .acceptArgument(true)
        .type(String.class)
        .description("Proxy user")
        .get();
  
  public static final Option PROXY_PASS = Option.of("-pp")
        .alias("--proxy-password")
        .acceptArgument(true)
        .type(String.class)
        .description("Proxy password")
        .get();
  
  public static final Option KEYSTORE = Option.of("-k")
        .alias("--keystore")
        .acceptArgument(true)
        .type(Path.class)
        .description("Keystore file")
        .get();
  
  public static final Option KEYSTORE_PASS = Option.of("-kp")
        .alias("--keystore-pass")
        .acceptArgument(true)
        .type(String.class)
        .description("Keystore password")
        .get();
  
  public static final Option PRIVATE_KEY = Option.of("-pk")
        .alias("--private-key")
        .acceptArgument(true)
        .type(Path.class)
        .description("Private key file")
        .<Path>action((a,v)->System.out.println(v.get().normalize()))
        .get();
  
  public static final Option PUBLIC_KEY = Option.of("-pub")
        .alias("--public-key")
        .acceptArgument(true)
        .type(Path.class)
        .description("Public key file")
        .get();
  
  public static final Option CRYPT_ALGORITHM = Option.of("-ca")
        .alias("--crypt-algo")
        .acceptArgument(true)
        .type(String.class)
        .description("Cryptography algorithm")
        .get();
  
  public static final Option SERVER_NAME = Option.of("-n")
        .alias("--server-name")
        .acceptArgument(true)
        .type(String.class)
        .description("Server (Http Header)")
        .get();
  
  public static final Option USER_AGENT = Option.of("-u")
        .alias("--user-agent")
        .acceptArgument(true)
        .type(String.class)
        .description("User-Agent (Http Header)")
        .get();
  public static final Option BUFFER_SIZE = Option.of("-b")
        .alias("--buffer")
        .acceptArgument(true)
        .type(int.class)
        .description("Buffer size in bytes")
        .get();
  
  public static final Option DIRECT_BUFFER = Option.of("-d")
        .alias("--direct-buffer")
        .acceptArgument(false)
        .description("Use direct buffers")
        .get();
  
  public static final Option THREAD_POOL_SIZE = Option.of("-tp")
        .alias("--thread-pool")
        .acceptArgument(true)
        .type(int.class)
        .description("Thread pool size")
        .get();
  
  public static final Option TIMEOUT = Option.of("-tm")
        .alias("--timeout")
        .acceptArgument(true)
        .type(long.class)
        .description("Server timeout in millis")
        .get();
  
  public static final Option QUIET = Option.of("-q")
        .alias("--quiet")
        .acceptArgument(false)
        .description("Dont print stuff")
        .get();
  
  public static Collection<Option> options() {
    return List.of(
        BUFFER_SIZE,
        CLIENT,
        CRYPT_ALGORITHM,
        DIRECT_BUFFER,
        KEYSTORE,
        KEYSTORE_PASS,
        PRIVATE_KEY,
        PROXY,
        PROXY_PASS,
        PROXY_USER,
        PUBLIC_KEY,
        QUIET,
        REMOTE,
        SERVER,
        SERVER_NAME,
        THREAD_POOL_SIZE,
        TIMEOUT,
        USER_AGENT
    );
  }
  
  public static Collection<OptionRule> rules() {
    return List.of(
        new IncompatibilityOptionRule(CLIENT, SERVER, KEYSTORE, KEYSTORE_PASS, SERVER_NAME),
        new IncompatibilityOptionRule(SERVER, CLIENT, USER_AGENT),
        new AndOptionRule(false, CLIENT, REMOTE, PRIVATE_KEY),
        new AndOptionRule(false, SERVER, REMOTE, PUBLIC_KEY),
        new DependencyOptionRule(PROXY, CLIENT),
        new DependencyOptionRule(PROXY_USER, PROXY),
        new DependencyOptionRule(PROXY_PASS, PROXY, PROXY_USER),
        new DependencyOptionRule(KEYSTORE, SERVER),
        new DependencyOptionRule(KEYSTORE_PASS, KEYSTORE),
        new DependencyOptionRule(USER_AGENT, CLIENT),
        new DependencyOptionRule(SERVER_NAME, SERVER),
        new DependencyOptionRule(TIMEOUT, SERVER),
        new XorOptionRule(true, CLIENT, SERVER)
    );
  }
  
}
