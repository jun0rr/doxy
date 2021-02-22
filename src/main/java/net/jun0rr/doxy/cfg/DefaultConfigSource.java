/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 * @author juno
 */
public class DefaultConfigSource implements ConfigSource {
  
  @Override
  public DoxyConfigBuilder load() throws Exception {
    return DoxyConfigBuilder.newBuilder()
        .bufferSize(DEFAULT_BUFFER_SIZE)
        .cryptAlgorithm(DEFAULT_CRYPT_ALGORITHM)
        .directBuffer(DEFAULT_DIRECT_BUFFER)
        .clientHost(DEFAULT_CLIENT_HOST)
        .serverHost(DEFAULT_SERVER_HOST)
        .remoteHost(DEFAULT_REMOTE_HOST)
        .keystorePath(DEFAULT_KEYSTORE_PATH)
        .keystorePassword(DEFAULT_KEYSTORE_PASS)
        .privateKeyPath(DEFAULT_PRIVATEKEY_PATH)
        .publicKeyPath(DEFAULT_PUBLICKEY_PATH)
        .serverName(DEFAULT_SERVER_NAME)
        .threadPoolSize(DEFAULT_THREAD_POOL_SIZE)
        .serverTimeout(DEFAULT_SERVER_TIMEOUT)
        .userAgent(DEFAULT_USER_AGENT);
  }
  
  
  public static final String LOCALHOST = "localhost";
  public static final String DEFAULT_SERVER_NAME = "Doxy-0.1-SNAPSHOT";
  public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0";
  public static final String DEFAULT_CRYPT_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
  public static final char[] DEFAULT_KEYSTORE_PASS = "p4ssw0rd".toCharArray();
  public static final String DEFAULT_KEYSTORE_NAME = "doxy-ks.jks";
  public static final String DEFAULT_PUBLICKEY_NAME = "doxy-pub.der";
  public static final String DEFAULT_PRIVATEKEY_NAME = "doxy-pk.der";
  public static final Path DEFAULT_KEYSTORE_PATH = Paths.get(System.getProperty("user.dir").concat("/").concat(DEFAULT_KEYSTORE_NAME));
  public static final Path DEFAULT_PRIVATEKEY_PATH = Paths.get(System.getProperty("user.dir").concat("/").concat(DEFAULT_PRIVATEKEY_NAME));
  public static final Path DEFAULT_PUBLICKEY_PATH = Paths.get(System.getProperty("user.dir").concat("/").concat(DEFAULT_PUBLICKEY_NAME));
  public static final boolean DEFAULT_DIRECT_BUFFER = false;
  public static final Host DEFAULT_CLIENT_HOST = Host.of(LOCALHOST, 3333);
  public static final Host DEFAULT_SERVER_HOST = Host.of(LOCALHOST, 443);
  public static final Host DEFAULT_REMOTE_HOST = Host.of(LOCALHOST, 6060);
  public static final int DEFAULT_BUFFER_SIZE = 8*1024;
  public static final long DEFAULT_SERVER_TIMEOUT = 8000;
  public static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
  
}
