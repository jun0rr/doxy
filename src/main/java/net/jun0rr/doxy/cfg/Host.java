/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Objects;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public interface Host {
  
  public String getHostname();
  
  public int getPort();
  
  public default InetSocketAddress toSocketAddr() {
    return new InetSocketAddress(getHostname(), getPort());
  }
  
  public default URL toURL(String proto) {
    return Unchecked.call(()->new URL(String.format("%s://%s:%d", proto, getHostname(), getPort())));
  }
  
  
  
  public static Host of(String host, int port) {
    return new HostImpl(host, port);
  }
  
  public static Host of(SocketAddress addr) {
    InetSocketAddress ia = (InetSocketAddress)addr;
    return new HostImpl(ia.getHostString(), ia.getPort());
  }
  
  public static Host of(String hostport) {
    try {
      int ip = hostport.lastIndexOf(":");
      return of(hostport.substring(0, ip), Integer.parseInt(hostport.substring(ip+1)));
    }
    catch(Exception e) {
      throw new IllegalArgumentException(String.format("Bad <host:port> String: '%s'", hostport));
    }
  }
  
  
  
  
  
  public class HostImpl implements Host {
  
    private final String hostname;

    private final int port;

    public HostImpl(String host, int port) {
      this.hostname = Objects.requireNonNull(host, "Bad null hostname");
      this.port = port;
      if(port < 1 || port > 65535) {
        throw new IllegalArgumentException("Bad port number: " + port);
      }
    }
    
    @Override
    public String getHostname() {
      return hostname;
    }
    
    @Override
    public int getPort() {
      return port;
    }
    
    @Override
    public int hashCode() {
      int hash = 7;
      hash = 79 * hash + Objects.hashCode(this.hostname);
      hash = 79 * hash + this.port;
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
      final HostImpl other = (HostImpl) obj;
      if (this.port != other.port) {
        return false;
      }
      return Objects.equals(this.hostname, other.hostname);
    }
    
    @Override
    public String toString() {
      return String.format("%s:%d", hostname, port);
    }
    
  }

}
