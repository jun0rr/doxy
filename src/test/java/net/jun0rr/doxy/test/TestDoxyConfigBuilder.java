/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.jun0rr.doxy.cfg.DefaultConfigSource;
import net.jun0rr.doxy.cfg.DoxyConfig;
import net.jun0rr.doxy.cfg.DoxyConfigBuilder;
import net.jun0rr.doxy.cfg.SysEnvConfigSource;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestDoxyConfigBuilder {
  
  @Test
  public void testLoadResourceProperties() throws Exception {
    System.out.println("------ testLoadResourceProperties ------");
    try {
      DoxyConfig cfg = DoxyConfigBuilder.newBuilder()
          .configSources()
          .fromResourceProps()
          .load()
          .build();
      System.out.println(cfg);
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  @Test
  public void testSysEnvComposedWithResourceProps() throws Exception {
    try {
      System.out.println("------ testSysEnvComposedWithResourceProps ------");
      Map<String,String> env = new HashMap<>();
      env.put(SysEnvConfigSource.ENV_CLIENT_HOST, "localhost:7777");
      env.put(SysEnvConfigSource.ENV_SERVER_HOST, "google.com:443");
      env.put(SysEnvConfigSource.ENV_REMOTE_HOST, "localhost:3620");
      env.put(SysEnvConfigSource.ENV_BUFFER_SIZE, "2048");
      env.put(SysEnvConfigSource.ENV_CRYPT_ALGORITHM, DefaultConfigSource.DEFAULT_CRYPT_ALGORITHM);
      env.put(SysEnvConfigSource.ENV_SERVER_TIMEOUT, "5000");
      env.put(SysEnvConfigSource.ENV_THREAD_POOL_SIZE, "2");
      DoxyConfig cfg = DoxyConfigBuilder.newBuilder()
          .configSources()
          .composeWithSystemEnv(env)
          .composeWithResourceProps()
          .fromComposedSource()
          .load()
          .build();
      System.out.println(cfg);
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
