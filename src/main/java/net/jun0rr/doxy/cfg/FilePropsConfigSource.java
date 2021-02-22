/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;


/**
 *
 * @author juno
 */
public class FilePropsConfigSource implements ConfigSource {
  
  private final Path path;
  
  private final Charset charset;
  
  public FilePropsConfigSource(Path path, Charset cs) {
    this.path = Objects.requireNonNull(path, "Bad null Path");
    this.charset = Objects.requireNonNull(cs, "Bad null Charset");
  }
  
  public FilePropsConfigSource(Path path) {
    this(path, StandardCharsets.UTF_8);
  }
  
  @Override
  public DoxyConfigBuilder load() throws Exception {
    Properties props = new Properties();
    try (BufferedReader rdr = Files.newBufferedReader(path, charset)) {
      props.load(rdr);
    }
    return new PropertiesConfigSource(props).load();
  }
  
}
