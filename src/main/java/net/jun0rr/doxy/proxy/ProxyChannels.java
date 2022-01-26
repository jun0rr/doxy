/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.proxy;

import io.netty.channel.Channel;
import java.util.Objects;
import us.pserver.tools.Hash;

/**
 *
 * @author juno
 */
public class ProxyChannels {
  /*
  private final String clientid;
  
  private final String targetid;
  
  private final ProxyEventChain client;
  
  private final ProxyEventChain target;

  private ProxyChannels(String clientid, String targetid, ProxyEventChain client, ProxyEventChain target) {
    this.clientid = Objects.requireNonNull(clientid);
    this.targetid = Objects.requireNonNull(targetid);
    this.client = Objects.requireNonNull(client);
    this.target = Objects.requireNonNull(target);
  }
  
  public static ProxyChannels of(ProxyEventChain client, ProxyEventChain target) {
    return new ProxyChannels(hash(client.channel()), hash(target.channel()), client, target);
  }
  
  public static String hash(Channel c) {
    return Hash.md5().of(String.format(
        "[%s->%s]", c.localAddress(), c.remoteAddress())
    );
  }
  
  public String clientid() {
    return clientid;
  }

  public String targetid() {
    return targetid;
  }

  public ProxyEventChain client() {
    return client;
  }
  
  public ProxyEventChain target() {
    return target;
  }
  
  public boolean matchClient(Channel c) {
    return clientid.equals(hash(c));
  }
  
  public boolean matchTarget(Channel c) {
    return targetid.equals(hash(c));
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.clientid);
    hash = 89 * hash + Objects.hashCode(this.targetid);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ProxyChannels other = (ProxyChannels) obj;
    if (!Objects.equals(this.clientid, other.clientid)) {
      return false;
    }
    if (!Objects.equals(this.targetid, other.targetid)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ProxyTarget{" + "localid=" + clientid + ", targetid=" + targetid + ", client=" + client.channel() + ", target=" + target.channel() + '}';
  }
  */
}
