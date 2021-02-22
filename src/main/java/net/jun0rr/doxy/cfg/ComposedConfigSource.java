/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.util.Collection;
import java.util.Objects;


/**
 *
 * @author juno
 */
public class ComposedConfigSource implements ConfigSource {
  
  private final Collection<ConfigSource> srcs;
  
  public ComposedConfigSource(Collection<ConfigSource> srcs) {
    this.srcs = Objects.requireNonNull(srcs, "Bad null ConfigSource collection");
  }

  @Override
  public DoxyConfigBuilder load() throws Exception {
    DoxyConfigBuilder bld = DoxyConfigBuilder.newBuilder();
    for(ConfigSource src : srcs) {
      DoxyConfigBuilder b = src.load();
      if(bld.getBufferSize() <= 0 && b.getBufferSize() > 0) {
        bld = bld.bufferSize(b.getBufferSize());
      }
      if(bld.getClientHost() == null && b.getClientHost() != null) {
        bld = bld.clientHost(b.getClientHost());
      }
      if(bld.getCryptAlgorithm() == null && b.getCryptAlgorithm() != null) {
        bld = bld.cryptAlgorithm(b.getCryptAlgorithm());
      }
      if(bld.getKeystorePassword() == null && b.getKeystorePassword() != null) {
        bld = bld.keystorePassword(b.getKeystorePassword());
      }
      if(bld.getKeystorePath() == null && b.getKeystorePath() != null) {
        bld = bld.keystorePath(b.getKeystorePath());
      }
      if(bld.getPrivateKeyPath() == null && b.getPrivateKeyPath() != null) {
        bld = bld.privateKeyPath(b.getPrivateKeyPath());
      }
      if(bld.getProxyHost() == null && b.getProxyHost() != null) {
        bld = bld.proxyHost(b.getProxyHost());
      }
      if(bld.getProxyPassword() == null && b.getProxyPassword() != null) {
        bld = bld.proxyPassword(b.getProxyPassword());
      }
      if(bld.getProxyUser() == null && b.getProxyUser() != null) {
        bld = bld.proxyUser(b.getProxyUser());
      }
      if(bld.getPublicKeyPath() == null && b.getPublicKeyPath() != null) {
        bld = bld.publicKeyPath(b.getPublicKeyPath());
      }
      if(bld.getRemoteHost() == null && b.getRemoteHost() != null) {
        bld = bld.remoteHost(b.getRemoteHost());
      }
      if(bld.getServerHost() == null && b.getServerHost() != null) {
        bld = bld.serverHost(b.getServerHost());
      }
      if(bld.getServerName() == null && b.getServerName() != null) {
        bld = bld.serverName(b.getServerName());
      }
      if(bld.getThreadPoolSize() <= 0 && b.getThreadPoolSize() > 0) {
        bld = bld.threadPoolSize(b.getThreadPoolSize());
      }
      if(bld.getUserAgent() == null && b.getUserAgent() != null) {
        bld = bld.userAgent(b.getUserAgent());
      }
      if(bld.getServerTimeout() <= 0 && b.getServerTimeout() > 0) {
        bld = bld.serverTimeout(b.getServerTimeout());
      }
      if(!bld.isDirectBufferEnabled() && b.isDirectBufferEnabled()) {
        bld = bld.directBuffer(b.isDirectBufferEnabled());
      }
    }
    return bld;
  }
  
}
