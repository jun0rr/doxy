/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import net.jun0rr.doxy.cfg.DoxyConfig;
import net.jun0rr.doxy.cfg.DoxyConfigBuilder;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.server.DoxyServer;
import org.junit.jupiter.api.Test;

/**
 *
 * @author juno
 */
public class TestDoxyServer {
  @Test
  public void test() throws Exception {
    try {
      DoxyConfig cfg = DoxyConfigBuilder.newBuilder()
          .configSources()
          .composeWithDefaults()
          .composeWithResourceProps()
          .fromComposedSource()
          .load()
          .build();
      new DoxyServer(DoxyEnvironment.of(cfg)).start().events().awaitShutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}
