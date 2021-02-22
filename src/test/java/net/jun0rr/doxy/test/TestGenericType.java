/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.lang.reflect.ParameterizedType;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestGenericType {
  
  public static <T> Class<T> ofGeneric(Consumer<T> cs) {
    ParameterizedType p = (ParameterizedType) cs.getClass().getGenericSuperclass();
    return (Class<T>) p.getActualTypeArguments()[0];
  }
  
  @Test
  public void method() {
    try {
      Consumer<Integer> printDouble = i->System.out.println(i*2);
      printDouble.accept(7);
      System.out.println(ofGeneric(printDouble));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
