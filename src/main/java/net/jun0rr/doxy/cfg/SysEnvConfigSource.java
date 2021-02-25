/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.util.Map;
import java.util.Objects;
import net.jun0rr.doxy.common.TypedProperties;


/**
 *
 * @author juno
 */
public class SysEnvConfigSource implements ConfigSource {
  
  private final Map<String,String> env;
  
  public SysEnvConfigSource(Map<String,String> env) {
    this.env = Objects.requireNonNull(env, "Bad null environment Map");
  }

  @Override
  public DoxyConfigBuilder load() throws Exception {
    TypedProperties prop = new TypedProperties();
    env.entrySet().stream()
        .filter(e->e.getKey().startsWith("DOXY"))
        .forEach(e->prop.setProperty(e.getKey(), e.getValue()));
    return DoxyConfigBuilder.newBuilder()
        .bufferSize(prop.getAsInt(ENV_BUFFER_SIZE))
        .clientHost(prop.getAsHost(ENV_CLIENT_HOST))
        .cryptAlgorithm(prop.getProperty(ENV_CRYPT_ALGORITHM))
        .directBuffer(prop.getAsBoolean(ENV_BUFFER_DIRECT))
        .keystorePath(prop.getAsPath(ENV_KEYSTORE_PATH))
        .keystorePassword(prop.getAsChars(ENV_KEYSTORE_PASS))
        .privateKeyPath(prop.getAsPath(ENV_PRIVATEKEY_PATH))
        .publicKeyPath(prop.getAsPath(ENV_PUBLICKEY_PATH))
        .proxyHost(prop.getAsHost(ENV_PROXY_HOST))
        .proxyUser(prop.getProperty(ENV_PROXY_USER))
        .proxyPassword(prop.getAsChars(ENV_PROXY_PASS))
        .remoteHost(prop.getAsHost(ENV_REMOTE_HOST))
        .serverHost(prop.getAsHost(ENV_SERVER_HOST))
        .serverName(prop.getProperty(ENV_SERVER_NAME))
        .quiet(prop.getAsBoolean(ENV_LOG_QUIET))
        .timeout(prop.getAsLong(ENV_SERVER_TIMEOUT))
        .threadPoolSize(prop.getAsInt(ENV_THREAD_POOL_SIZE))
        .userAgent(prop.getProperty(ENV_USERAGENT));
  }
  
  @Override
  public int weight() {
    return WEIGHT;
  }

  
  public static final int WEIGHT = 2;
  public static final String ENV_CLIENT_HOST = "DOXY_CLIENT_HOST";
  public static final String ENV_USERAGENT = "DOXY_USERAGENT";
  public static final String ENV_SERVER_NAME = "DOXY_SERVER_NAME";
  public static final String ENV_SERVER_HOST = "DOXY_SERVER_HOST";
  public static final String ENV_SERVER_TIMEOUT = "DOXY_SERVER_TIMEOUT";
  public static final String ENV_REMOTE_HOST = "DOXY_REMOTE_HOST";
  public static final String ENV_PROXY_HOST = "DOXY_PROXY_HOST";
  public static final String ENV_PROXY_USER = "DOXY_PROXY_USER";
  public static final String ENV_PROXY_PASS = "DOXY_PROXY_PASS";
  public static final String ENV_KEYSTORE_PATH = "DOXY_KEYSTORE_PATH";
  public static final String ENV_KEYSTORE_PASS = "DOXY_KEYSTORE_PASS";
  public static final String ENV_PUBLICKEY_PATH = "DOXY_PUBLICKEY_PATH";
  public static final String ENV_PRIVATEKEY_PATH = "DOXY_PRIVATEKEY_PATH";
  public static final String ENV_CRYPT_ALGORITHM = "DOXY_CRYPT_ALGORITHM";
  public static final String ENV_BUFFER_SIZE = "DOXY_BUFFER_SIZE";
  public static final String ENV_BUFFER_DIRECT = "DOXY_BUFFER_DIRECT";
  public static final String ENV_LOG_QUIET = "DOXY_LOG_QUIET";
  public static final String ENV_THREAD_POOL_SIZE = "DOXY_THREAD_POOL_SIZE";
  
}
