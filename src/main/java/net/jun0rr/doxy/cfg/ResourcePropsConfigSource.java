/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;


/**
 *
 * @author juno
 */
public class ResourcePropsConfigSource implements ConfigSource {
  
  public static final int WEIGHT = 1;
  
  public static final String DEFAULT_PROPS_FILE_NAME = "doxy.properties";
  
  private final String filename;
  
  private final Charset charset;
  
  public ResourcePropsConfigSource(String filename, Charset cs) {
    this.filename = Objects.requireNonNull(filename, "Bad null Path");
    this.charset = Objects.requireNonNull(cs, "Bad null Charset");
  }
  
  public ResourcePropsConfigSource(String filename) {
    this(filename, StandardCharsets.UTF_8);
  }
  
  public ResourcePropsConfigSource() {
    this(DEFAULT_PROPS_FILE_NAME, StandardCharsets.UTF_8);
  }
  
  @Override
  public DoxyConfigBuilder load() throws Exception {
    Properties props = new Properties();
    try (InputStreamReader rdr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename), charset)) {
      props.load(rdr);
    }
    return new PropertiesConfigSource(props, weight()).load();
  }

  @Override
  public int weight() {
    return WEIGHT;
  }

}
