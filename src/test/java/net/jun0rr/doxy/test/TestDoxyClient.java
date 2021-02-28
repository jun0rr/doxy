/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import net.jun0rr.doxy.cfg.ConfigSource;
import net.jun0rr.doxy.cfg.ConfigSourceBuilder;
import net.jun0rr.doxy.cfg.DoxyConfigBuilder;
import net.jun0rr.doxy.client.DoxyClient;
import net.jun0rr.doxy.common.DoxyEnvironment;
import org.junit.jupiter.api.Test;

/**
 *
 * @author juno
 */
public class TestDoxyClient {
  
  public DoxyEnvironment environment() throws Exception {
    try {
      ConfigSource src = new ConfigSourceBuilder().composeWithDefaults()
          .composeWithResourceProps("doxy-client.properties")
          .fromComposedSource();
      DoxyConfigBuilder bld = src.load();
      return DoxyEnvironment.of(bld.proxyHost(null)
          //.serverHost("localhost", 3333)
          //.remoteHost("localhost", 6060)
          //.clientHost("localhost", 4444)
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
      DoxyClient cli = new DoxyClient(environment());
      cli.start().eventChain().awaitShutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
