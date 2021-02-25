/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 *
 * @author Juno
 */
public class ConfigSourceBuilder {
  
  private final List<ConfigSource> srcs;
  
  public ConfigSourceBuilder() {
    this.srcs = new LinkedList<>();
  }
  
  public ConfigSource fromProperties(Properties prop) {
    return new PropertiesConfigSource(prop, 1);
  }
  
  public ConfigSource fromFile(Path path, Charset cs) {
    return new FilePropsConfigSource(path, cs);
  }
  
  public ConfigSource fromFile(Path path) {
    return new FilePropsConfigSource(path);
  }
  
  public ConfigSource fromDefaults() {
    return new DefaultConfigSource();
  }
  
  public ConfigSource fromSystemEnv(Map<String,String> env) {
    return new SysEnvConfigSource(env);
  }
  
  public ConfigSource fromSystemEnv() {
    return new SysEnvConfigSource(System.getenv());
  }
  
  public ConfigSource fromResourceProps(String filename, Charset cs) {
    return new ResourcePropsConfigSource(filename, cs);
  }
  
  public ConfigSource fromResourceProps(String filename) {
    return new ResourcePropsConfigSource(filename);
  }
  
  public ConfigSource fromResourceProps() {
    return new ResourcePropsConfigSource();
  }
  
  public ConfigSource fromComposedSource() {
    return new ComposedConfigSource(srcs);
  }
  
  public ConfigSourceBuilder composeWith(ConfigSource src) {
    srcs.add(src);
    return this;
  }
  
  public ConfigSourceBuilder composeWithProperties(Properties prop) {
    srcs.add(fromProperties(prop));
    return this;
  }
  
  public ConfigSourceBuilder composeWithFile(Path path, Charset cs) {
    srcs.add(fromFile(path, cs));
    return this;
  }
  
  public ConfigSourceBuilder composeWithFile(Path path) {
    srcs.add(fromFile(path));
    return this;
  }
  
  public ConfigSourceBuilder composeWithSystemEnv(Map<String,String> env) {
    srcs.add(fromSystemEnv(env));
    return this;
  }
  
  public ConfigSourceBuilder composeWithSystemEnv() {
    srcs.add(fromSystemEnv());
    return this;
  }
  
  public ConfigSourceBuilder composeWithDefaults() {
    srcs.add(fromDefaults());
    return this;
  }
  
  public ConfigSourceBuilder composeWithResourceProps(String filename, Charset cs) {
    srcs.add(fromResourceProps(filename, cs));
    return this;
  }
  
  public ConfigSourceBuilder composeWithResourceProps(String filename) {
    srcs.add(fromResourceProps(filename));
    return this;
  }
  
  public ConfigSourceBuilder composeWithResourceProps() {
    srcs.add(fromResourceProps());
    return this;
  }
  
}
