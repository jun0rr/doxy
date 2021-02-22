/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.nio.file.Path;
import java.util.Objects;


/**
 *
 * @author Juno
 */
public class DoxyConfigBuilder {
  
  private final Host host;
  
  private final Host server;
  
  private final Host remote;
  
  private final Host proxy;
  
  private final String proxyUser;
  
  private final char[] proxyPass;
  
  private final Path pkpath;
  
  private final Path pubpath;
  
  private final Path kspath;
  
  private final char[] kspass;
  
  private final int threadPoolSize;
  
  private final int bufferSize;
  
  private final long timeout;
  
  private final boolean directBuffer;
  
  private final String serverName;
  
  private final String userAgent;
  
  private final String cryptAlg;
  
  
  public DoxyConfigBuilder() {
    this.host = null;
    this.server = null;
    this.remote = null;
    this.proxy = null;
    this.threadPoolSize = 0;
    this.proxyUser = null;
    this.proxyPass = null;
    this.serverName = null;
    this.userAgent = null;
    this.bufferSize = 0;
    this.directBuffer = false;
    this.kspath = null;
    this.pkpath = null;
    this.pubpath = null;
    this.kspass = null;
    this.cryptAlg = null;
    this.timeout = 0;
  }


  public DoxyConfigBuilder(Host host, Host server, Host remote, Host proxy, String proxyUser, char[] proxyPass, Path privateKey, Path publicKey, Path keystore, char[] keystorePass, String serverName, String userAgent, String cryptAlg, int threadPoolSize, int bufferSize, boolean directBuffer, long timeout) {
    this.host = host;
    this.server = server;
    this.remote = remote;
    this.proxy = proxy;
    this.threadPoolSize = threadPoolSize;
    this.proxyUser = proxyUser;
    this.proxyPass = proxyPass;
    this.serverName = serverName;
    this.userAgent = userAgent;
    this.bufferSize = bufferSize;
    this.directBuffer = directBuffer;
    this.kspath = keystore;
    this.pkpath = privateKey;
    this.pubpath = publicKey;
    this.kspass = keystorePass;
    this.cryptAlg = cryptAlg;
    this.timeout = timeout;
  }
  
  
  public DoxyConfigBuilder clientHost(String hostname, int port) {
    return new DoxyConfigBuilder(Host.of(hostname, port), server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder clientHost(Host host) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder serverHost(String hostname, int port) {
    return new DoxyConfigBuilder(host, Host.of(hostname, port), remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder serverHost(Host server) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder remoteHost(String hostname, int port) {
    return new DoxyConfigBuilder(host, server, Host.of(hostname, port), proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder remoteHost(Host remote) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder proxyHost(String hostname, int port) {
    return new DoxyConfigBuilder(host, server, remote, Host.of(hostname, port), proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder proxyHost(Host proxy) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder threadPoolSize(int threadPoolSize) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder serverTimeout(long timeout) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder bufferSize(int bufferSize) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder directBuffer(boolean directBuffer) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder proxyUser(String proxyUser) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder proxyPassword(char[] proxyPass) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder serverName(String serverName) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder userAgent(String userAgent) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder privateKeyPath(Path pkpath) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder publicKeyPath(Path pubpath) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder keystorePath(Path kspath) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder keystorePassword(char[] kspass) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public DoxyConfigBuilder cryptAlgorithm(String cryptAlg) {
    return new DoxyConfigBuilder(host, server, remote, proxy, proxyUser, proxyPass, pkpath, pubpath, kspath, kspass, serverName, userAgent, cryptAlg, threadPoolSize, bufferSize, directBuffer, timeout);
  }
  
  public Host getClientHost() {
    return host;
  }
  
  public Host getServerHost() {
    return server;
  }
  
  public Host getRemoteHost() {
    return remote;
  }
  
  public Host getProxyHost() {
    return proxy;
  }
  
  public String getProxyUser() {
    return proxyUser;
  }
  
  public char[] getProxyPassword() {
    return proxyPass;
  }
  
  public int getBufferSize() {
    return bufferSize;
  }
  
  public int getThreadPoolSize() {
    return threadPoolSize;
  }
  
  public boolean isDirectBufferEnabled() {
    return directBuffer;
  }
  
  public Path getPrivateKeyPath() {
    return pkpath;
  }
  
  public Path getPublicKeyPath() {
    return pubpath;
  }
  
  public Path getKeystorePath() {
    return kspath;
  }
  
  public char[] getKeystorePassword() {
    return kspass;
  }
  
  public String getServerName() {
    return serverName;
  }
  
  public long getServerTimeout() {
    return timeout;
  }
  
  public String getUserAgent() {
    return userAgent;
  }
  
  public String getCryptAlgorithm() {
    return userAgent;
  }
  
  public ConfigSourceBuilder setupConfigSources() {
    return new ConfigSourceBuilder();
  }
  
  public DoxyConfig build() {
    Objects.requireNonNull(host, "Bad null Host");
    Objects.requireNonNull(server, "Bad null Server Host");
    Objects.requireNonNull(cryptAlg, "Bad null cryptography algorithm");
    if(threadPoolSize <= 0) throw new IllegalStateException("Bad thread pool size: " + threadPoolSize);
    if(bufferSize <= 0) throw new IllegalStateException("Bad buffer size: " + bufferSize);
    if(timeout <= 0) throw new IllegalStateException("Bad server timeout: " + timeout);
    return DoxyConfig.of(
        host, 
        server, 
        remote, 
        ProxyConfig.of(proxy, proxyUser, proxyPass),
        SecurityConfig.of(pkpath, pubpath, kspath, kspass, cryptAlg),
        serverName,
        userAgent,
        threadPoolSize, 
        bufferSize, 
        directBuffer,
        timeout
    );
  }
  
  
  
  public static DoxyConfigBuilder newBuilder() {
    return new DoxyConfigBuilder();
  }
  
}
