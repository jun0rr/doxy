/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.handler.codec.http.QueryStringEncoder;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 *
 * @author juno
 */
public class TestQueryStringEnconder {
  
  @Test
  public void test() throws URISyntaxException {
    QueryStringEncoder enc = new QueryStringEncoder("/echo", StandardCharsets.UTF_8);
    enc.addParam("message", "Hello World /");
    enc.addParam("type", "Greet Message");
    System.out.println(enc.toString());
    System.out.println(enc.toUri());
  }
  
}
