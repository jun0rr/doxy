/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.util.Objects;
import java.util.Properties;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_BUFFER_SIZE;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_CLIENT_HOST;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_CRYPT_ALGORITHM;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_LOG_QUIET;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_REMOTE_HOST;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_SERVER_HOST;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_SERVER_NAME;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_USER_AGENT;
import net.jun0rr.doxy.common.TypedProperties;
import static net.jun0rr.doxy.cfg.DefaultConfigSource.DEFAULT_TIMEOUT;


/**
 *
 * @author juno
 */
public class PropertiesConfigSource implements ConfigSource {
  
  private final TypedProperties properties;
  
  private final int weight;
  
  public PropertiesConfigSource(Properties props, int weight) {
    this.properties = new TypedProperties(Objects.requireNonNull(props, "Bad null Properties"));
    this.weight = weight;
  }
  
  @Override
  public DoxyConfigBuilder load() throws Exception {
    return DoxyConfigBuilder.newBuilder()
        .bufferSize(properties.getAsInt(PROP_BUFFER_SIZE, DEFAULT_BUFFER_SIZE))
        .cryptAlgorithm(properties.getProperty(PROP_SECURITY_CRYPT_ALGORITHM, DEFAULT_CRYPT_ALGORITHM))
        .directBuffer(properties.getAsBoolean(PROP_BUFFER_DIRECT))
        .clientHost(properties.getAsHost(PROP_CLIENT_HOST, DEFAULT_CLIENT_HOST))
        .keystorePath(properties.getAsPath(PROP_SECURITY_KEYSTORE_PATH))
        .keystorePassword(properties.getAsChars(PROP_SECURITY_KEYSTORE_PASS))
        .privateKeyPath(properties.getAsPath(PROP_SECURITY_PRIVATEKEY_PATH))
        .publicKeyPath(properties.getAsPath(PROP_SECURITY_PUBLICKEY_PATH))
        .proxyHost(properties.getAsHost(PROP_PROXY_HOST))
        .proxyUser(properties.getProperty(PROP_PROXY_USER))
        .proxyPassword(properties.getAsChars(PROP_PROXY_PASS))
        .remoteHost(properties.getAsHost(PROP_REMOTE_HOST, DEFAULT_REMOTE_HOST))
        .serverHost(properties.getAsHost(PROP_SERVER_HOST, DEFAULT_SERVER_HOST))
        .serverName(properties.getProperty(PROP_SERVER_NAME, DEFAULT_SERVER_NAME))
        .threadPoolSize(properties.getAsInt(PROP_THREADPOOL_SIZE))
        .timeout(properties.getAsLong(PROP_SERVER_TIMEOUT, DEFAULT_TIMEOUT))
        .quiet(properties.getAsBoolean(PROP_LOG_QUIET, DEFAULT_LOG_QUIET))
        .userAgent(properties.getProperty(PROP_USERAGENT, DEFAULT_USER_AGENT));
  }
  
  @Override
  public int weight() {
    return weight;
  }

  
  public static final String PROP_CLIENT_HOST = "client.host";
  public static final String PROP_USERAGENT = "useragent";
  public static final String PROP_SERVER_NAME = "server.name";
  public static final String PROP_SERVER_HOST = "server.host";
  public static final String PROP_SERVER_TIMEOUT = "server.timeout";
  public static final String PROP_REMOTE_HOST = "remote.host";
  public static final String PROP_PROXY_HOST = "proxy.host";
  public static final String PROP_PROXY_USER = "proxy.user";
  public static final String PROP_PROXY_PASS = "proxy.pass";
  public static final String PROP_SECURITY_KEYSTORE_PATH = "security.keystore.path";
  public static final String PROP_SECURITY_KEYSTORE_PASS = "security.keystore.pass";
  public static final String PROP_SECURITY_PUBLICKEY_PATH = "security.publickey.path";
  public static final String PROP_SECURITY_PRIVATEKEY_PATH = "security.privatekey.path";
  public static final String PROP_SECURITY_CRYPT_ALGORITHM = "security.crypt.algorithm";
  public static final String PROP_BUFFER_SIZE = "buffer.size";
  public static final String PROP_BUFFER_DIRECT = "buffer.direct";
  public static final String PROP_LOG_QUIET = "log.quiet";
  public static final String PROP_THREADPOOL_SIZE = "threadpool.size";
  
}
