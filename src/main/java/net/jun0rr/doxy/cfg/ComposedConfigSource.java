/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
    List<ConfigSource> ordered = new LinkedList(srcs);
    Collections.sort(ordered);
    for(ConfigSource src : ordered) {
      DoxyConfigBuilder b = src.load();
      if(b.getBufferSize() > 0) {
        bld = bld.bufferSize(b.getBufferSize());
      }
      if(b.getClientHost() != null) {
        bld = bld.clientHost(b.getClientHost());
      }
      if(b.getCryptAlgorithm() != null) {
        bld = bld.cryptAlgorithm(b.getCryptAlgorithm());
      }
      if(b.getKeystorePassword() != null) {
        bld = bld.keystorePassword(b.getKeystorePassword());
      }
      if(b.getKeystorePath() != null) {
        bld = bld.keystorePath(b.getKeystorePath());
      }
      if(b.getPrivateKeyPath() != null) {
        bld = bld.privateKeyPath(b.getPrivateKeyPath());
      }
      if(b.getProxyHost() != null) {
        bld = bld.proxyHost(b.getProxyHost());
      }
      if(b.getProxyPassword() != null) {
        bld = bld.proxyPassword(b.getProxyPassword());
      }
      if(b.getProxyUser() != null) {
        bld = bld.proxyUser(b.getProxyUser());
      }
      if(b.getPublicKeyPath() != null) {
        bld = bld.publicKeyPath(b.getPublicKeyPath());
      }
      if(b.getRemoteHost() != null) {
        bld = bld.remoteHost(b.getRemoteHost());
      }
      if(b.getServerHost() != null) {
        bld = bld.serverHost(b.getServerHost());
      }
      if(b.getServerName() != null) {
        bld = bld.serverName(b.getServerName());
      }
      if(b.getThreadPoolSize() > 0) {
        bld = bld.threadPoolSize(b.getThreadPoolSize());
      }
      if(b.getUserAgent() != null) {
        bld = bld.userAgent(b.getUserAgent());
      }
      if(b.getTimeout() > 0) {
        bld = bld.timeout(b.getTimeout());
      }
      if(!bld.isDirectBufferEnabled() && b.isDirectBufferEnabled()) {
        bld = bld.directBuffer(b.isDirectBufferEnabled());
      }
      if(!bld.isQuietEnabled() && b.isQuietEnabled()) {
        bld = bld.quiet(b.isQuietEnabled());
      }
    }
    return bld;
  }
  
  @Override
  public int weight() {
    return srcs.stream()
        .mapToInt(ConfigSource::weight)
        .max().orElse(1);
  }
  
}
