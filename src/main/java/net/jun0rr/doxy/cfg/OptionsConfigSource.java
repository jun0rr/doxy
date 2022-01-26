/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import net.jun0rr.doxy.DoxyAppOptions;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_CLIENT_HOST;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_CRYPT_ALGORITHM;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_REMOTE_HOST;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_SERVER_HOST;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_SERVER_NAME;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_USER_AGENT;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_BUFFER_DIRECT;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_CLIENT_HOST;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_PROXY_HOST;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_PROXY_PASS;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_PROXY_USER;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_REMOTE_HOST;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SECURITY_CRYPT_ALGORITHM;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SECURITY_KEYSTORE_PASS;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SECURITY_KEYSTORE_PATH;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SECURITY_PRIVATEKEY_PATH;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SECURITY_PUBLICKEY_PATH;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SERVER_HOST;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SERVER_NAME;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_SERVER_TIMEOUT;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_THREADPOOL_SIZE;
import static net.jun0rr.doxy.cfg.PropertiesConfigSource.PROP_USERAGENT;
import net.jun0rr.doxy.opt.ParsedOption;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_TIMEOUT;


/**
 *
 * @author juno
 */
public class OptionsConfigSource implements ConfigSource {
  
  public static final int WEIGHT = 5;
  
  private final Collection<ParsedOption> options;
  
  public OptionsConfigSource(Collection<ParsedOption> options) {
    this.options = Objects.requireNonNull(options);
  }
  
  @Override
  public DoxyConfigBuilder load() throws Exception {
    DoxyConfigBuilder bld = DoxyConfigBuilder.newBuilder();
    bld = options.stream()
        .filter(DoxyAppOptions.BUFFER_SIZE::equals)
        .filter(p->p.value().isPresent())
        .mapToInt(p->p.<Integer>value().get())
        .mapToObj(bld::bufferSize)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.CLIENT::equals)
        .filter(p->p.value().isPresent())
        .mapToInt(p->p.<Integer>value().get())
        .mapToObj(bld::bufferSize)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.CRYPT_ALGORITHM::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<String>value().get())
        .map(bld::cryptAlgorithm)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.DIRECT_BUFFER::equals)
        .map(p->true)
        .map(bld::directBuffer)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.KEYSTORE::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<Path>value().get())
        .map(bld::keystorePath)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.KEYSTORE_PASS::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<String>value().get().toCharArray())
        .map(bld::keystorePassword)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.PRIVATE_KEY::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<Path>value().get())
        .map(bld::privateKeyPath)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.PROXY::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<Host>value().get())
        .map(bld::proxyHost)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.PROXY_PASS::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<String>value().get().toCharArray())
        .map(bld::proxyPassword)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.PROXY_USER::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<String>value().get())
        .map(bld::proxyUser)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.PUBLIC_KEY::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<Path>value().get())
        .map(bld::publicKeyPath)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.QUIET::equals)
        .map(p->true)
        .map(bld::quiet)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.REMOTE::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<Host>value().get())
        .map(bld::remoteHost)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.SERVER::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<Host>value().get())
        .map(bld::serverHost)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.SERVER_NAME::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<String>value().get())
        .map(bld::serverName)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.THREAD_POOL_SIZE::equals)
        .filter(p->p.value().isPresent())
        .mapToInt(p->p.<Integer>value().get())
        .mapToObj(bld::threadPoolSize)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.TIMEOUT::equals)
        .filter(p->p.value().isPresent())
        .mapToLong(p->p.<Integer>value().get())
        .mapToObj(bld::timeout)
        .findAny()
        .orElse(bld);
    
    bld = options.stream()
        .filter(DoxyAppOptions.USER_AGENT::equals)
        .filter(p->p.value().isPresent())
        .map(p->p.<String>value().get())
        .map(bld::userAgent)
        .findAny()
        .orElse(bld);
    
    return bld;
  }
  
  @Override
  public int weight() {
    return WEIGHT;
  }

}
