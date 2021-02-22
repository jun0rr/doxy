/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import us.pserver.tools.io.ResourceLoader;

/**
 *
 * @author juno
 */
public class TestResourceLoader {
  
  @Test
  public void loadProperties() {
    System.out.println(ResourceLoader.self().loadStringContent("doxy.properties"));
  }
  
  @Test
  public void loadPropertiesClass() throws URISyntaxException {
    Path kspath = Paths.get(getClass().getClassLoader().getResource("doxy.jks").toURI());
    System.out.println(kspath);
  }
  
}
