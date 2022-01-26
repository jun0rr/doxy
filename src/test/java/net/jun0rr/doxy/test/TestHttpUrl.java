/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jun0rr.doxy.http.HttpUrl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author juno
 */
public class TestHttpUrl {
  
  @Test
  public void test0() {
    HttpUrl url = new HttpUrl("http://localhost");
    System.out.println(url);
    Assertions.assertEquals("http", url.getProtocol());
    Assertions.assertEquals("localhost", url.getHost());
    Assertions.assertEquals(80, url.getPort());
    Assertions.assertEquals("/", url.getURI());
  }
  
  @Test
  public void test1() {
    HttpUrl url = new HttpUrl("https://localhost:4321/hello");
    System.out.println(url);
    Assertions.assertEquals("https", url.getProtocol());
    Assertions.assertEquals("localhost", url.getHost());
    Assertions.assertEquals(4321, url.getPort());
    Assertions.assertEquals("/hello", url.getURI());
  }
  
  @Test
  public void test2() {
    HttpUrl url = new HttpUrl("https://www.bb.com.br");
    System.out.println(url);
    Assertions.assertEquals("https", url.getProtocol());
    Assertions.assertEquals("www.bb.com.br", url.getHost());
    Assertions.assertEquals(443, url.getPort());
    Assertions.assertEquals("/", url.getURI());
  }
  
  @Test
  public void test4() {
    Matcher m = Pattern.compile("(http[s]?):\\/\\/([a-zA-Z0-9\\.-]+):?([0-9]{2,5})?(\\/.*)?").matcher("https://localhost:4321/hello/world");
    System.out.println("Matcher.find(): " + m.find());
    for(int i =0; i <= m.groupCount(); i++) {
      System.out.printf("Matcher.group(%d): %s%n", i, m.group(i));
    }
  }
  
  @Test
  public void test5() {
    Matcher m = Pattern.compile("(http[s]?)://([a-zA-Z0-9\\.-]+):?([0-9]{2,5})?(/.*)?").matcher("http://localhost/");
    System.out.println("Matcher.find(): " + m.find());
    for(int i =0; i <= m.groupCount(); i++) {
      System.out.printf("Matcher.group(%d): %s%n", i, m.group(i));
    }
  }
  
  @Test
  public void test6() {
    Map<String,Integer> m = new HashMap<>();
    for(int i = 0; i < 10; i++) {
      m.put(String.format("#%d", i), i);
    }
    System.out.printf("HashMap( %d )%n", m.size());
    m.entrySet().stream().forEach(e->System.out.printf("  - %s: %s%n", e.getKey(), e.getValue()));
    System.out.printf("Reversed HashMap( %d )%n", m.size());
    m.entrySet().stream()
        .sorted((e,f)->f.getKey().compareTo(e.getKey()))
        .forEach(e->System.out.printf("  - %s: %s%n", e.getKey(), e.getValue()));
    m = new TreeMap<>();
    for(int i = 10; i > 0; i--) {
      m.put(String.format("#%d", i), i);
    }
    System.out.printf("TreeMap( %d )%n", m.size());
    m.entrySet().stream().forEach(e->System.out.printf("  - %s: %s%n", e.getKey(), e.getValue()));
    System.out.printf("Reversed TreeMap( %d )%n", m.size());
    m.entrySet().stream()
        .sorted((e,f)->f.getKey().compareTo(e.getKey()))
        .forEach(e->System.out.printf("  - %s: %s%n", e.getKey(), e.getValue()));
  }
  
}
