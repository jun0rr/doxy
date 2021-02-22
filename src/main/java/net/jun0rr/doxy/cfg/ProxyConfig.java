/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.util.Arrays;
import java.util.Objects;
import us.pserver.tools.StringPad;


/**
 *
 * @author Juno
 */
public interface ProxyConfig {
  
  public Host getProxyHost();
  
  public String getProxyUser();
  
  public char[] getProxyPassword();
  
  
  
  public static ProxyConfig of(String host, int port, String user, char[] pass) {
    return new ProxyConfigImpl(Host.of(host, port), user, pass);
  }
  
  public static ProxyConfig of(Host host, String user, char[] pass) {
    return new ProxyConfigImpl(host, user, pass);
  }
  
  public static ProxyConfig of(Host host) {
    return new ProxyConfigImpl(host, null, null);
  }
  
  
  
  
  
  public static class ProxyConfigImpl implements ProxyConfig {
    
    private final Host host;
    
    private final String user;
    
    private char[] pass;
    
    public ProxyConfigImpl(Host host, String user, char[] pass) {
      this.host = Objects.requireNonNull(host, "Bad null proxy host");
      this.user = user;
      this.pass = pass;
    }
    
    @Override
    public Host getProxyHost() {
      return host;
    }
    
    @Override
    public String getProxyUser() {
      return user;
    }
    
    @Override
    public char[] getProxyPassword() {
      return pass;
    }
    
    @Override
    public int hashCode() {
      int hash = 7;
      hash = 37 * hash + Objects.hashCode(this.host);
      hash = 37 * hash + Objects.hashCode(this.user);
      hash = 37 * hash + Arrays.hashCode(this.pass);
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
      final ProxyConfig other = (ProxyConfig) obj;
      if (!Objects.equals(this.user, other.getProxyUser())) {
        return false;
      }
      if (!Objects.equals(this.host, other.getProxyHost())) {
        return false;
      }
      return Arrays.equals(this.pass, other.getProxyPassword());
    }
    
    @Override
    public String toString() {
      return "ProxyConfig{" + "host=" + host + ", user=" + user + ", pass=" + StringPad.of("").lpad("*", pass.length) + '}';
    }
    
  }
  
}
