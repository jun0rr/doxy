/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import net.jun0rr.doxy.http.util.RequestParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestUriRequestParam {
  
  @Test
  public void requestFromUriPattern() {
    try {
      String uri = "/evt/422/2020/179886";
      String pattern = "/evt/{subproj}/{aaevt}/{nrevt}";
      RequestParam pars = RequestParam.fromUriPattern(pattern, uri);
      System.out.println(pars);
      Assertions.assertEquals(422, pars.get("subproj").getAsInt());
      Assertions.assertEquals(2020, pars.get("aaevt").getAsObject());
      Assertions.assertEquals(179886L, pars.get("nrevt").getAsLong());
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
