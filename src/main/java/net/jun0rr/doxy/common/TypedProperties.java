/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author Juno
 */
public class TypedProperties extends Properties {
  
  public TypedProperties() {
    super();
  }
  
  public TypedProperties(Properties defaults) {
    super(defaults);
  }
  
  public int getAsInt(String prop) {
    return getAsInt(prop, -1);
  }
  
  public int getAsInt(String prop, int def) {
    try {
      return Integer.parseInt(this.getProperty(prop));
    }
    catch(Exception e) {
      return def;
    }
  }
  
  public long getAsLong(String prop) {
    return getAsLong(prop, -1L);
  }
  
  public long getAsLong(String prop, long def) {
    try {
      return Long.parseLong(this.getProperty(prop));
    }
    catch(Exception e) {
      return def;
    }
  }
  
  public Host getAsHost(String prop) {
    return getAsHost(prop, null);
  }
  
  public Host getAsHost(String prop, Host host) {
    String shost = this.getProperty(prop);
    return shost == null || !shost.contains(":") ? host : Host.of(shost);
  }
  
  public char[] getAsChars(String prop) {
    return getAsChars(prop, new char[0]);
  }
  
  public char[] getAsChars(String prop, char[] def) {
    String val = this.getProperty(prop);
    return val != null ? val.toCharArray() : def;
  }
  
  public boolean getAsBoolean(String prop) {
    return getAsBoolean(prop, false);
  }
  
  public boolean getAsBoolean(String prop, boolean def) {
    try {
      return Boolean.parseBoolean(this.getProperty(prop));
    }
    catch(Exception e) {
      return def;
    }
  }
  
  public Path getAsPath(String prop, Path def) {
    String spath = getProperty(prop);
    return spath == null ? def : Paths.get(spath);
  }
  
  public Path getAsPath(String prop) {
    return getAsPath(prop, null);
  }
  
}
