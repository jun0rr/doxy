/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestJsonObject {
  @Test
  public void method() {
    JsonObject obj = new JsonObject();
    obj.addProperty("hello", "world");
    obj.addProperty("hello", false);
    System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
  }
}
