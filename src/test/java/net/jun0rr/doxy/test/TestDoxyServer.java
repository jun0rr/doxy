/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import net.jun0rr.doxy.cfg.ConfigSource;
import net.jun0rr.doxy.cfg.ConfigSourceBuilder;
import net.jun0rr.doxy.cfg.DoxyConfigBuilder;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.server.DoxyServer;
import org.junit.jupiter.api.Test;

/**
 *
 * @author juno
 */
public class TestDoxyServer {
  
  public DoxyEnvironment environment() throws Exception {
    try {
      ConfigSource src = new ConfigSourceBuilder().composeWithDefaults()
          .composeWithResourceProps("doxy-server.properties")
          .fromComposedSource();
      DoxyConfigBuilder bld = src.load();
      return DoxyEnvironment.of(bld
          //.proxyHost(null)
          //.remoteHost("localhost", 6060)
          //.serverHost("localhost", 3333)
          .build()
      );
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  @Test
  public void test() throws Exception {
    try {
      DoxyServer server = new DoxyServer(environment());
      server.start().eventChain().awaitShutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
