/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestJavaTime {
  @Test
  public void localDate() {
    String date = "29/03/2020";
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    TemporalAccessor ta = df.parse(date);
    System.out.println(date + " --> " + ta);
    System.out.println(ta.getClass().getName());
    System.out.println(LocalDate.from(ta));
    System.out.println(Instant.now());
  }
}
