/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestEnvironment {
  
  @Test
  public void getSystemEnv() {
    System.out.println(System.getenv("DOXY_HOST"));
  }
  
  @Test
  public void method() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
    try {
      Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
      //env.putAll(newenv);
      Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true);
      Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
      //cienv.putAll(newenv);
    } catch (NoSuchFieldException e) {
      Class[] classes = Collections.class.getDeclaredClasses();
      Map<String, String> env = System.getenv();
      for(Class cl : classes) {
        if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          Object obj = field.get(env);
          Map<String, String> map = (Map<String, String>) obj;
          map.clear();
          //map.putAll(newenv);
        }
      }
    }
  }
  
}
