/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.net.InetAddress;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.common.Range;
import net.jun0rr.doxy.common.StringValue;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestStringValue {
  
  @Test
  public void inetAddress() {
    System.out.println("---- inetAddress ----");
    StringValue sv = StringValue.of("127.0.0.1");
    System.out.println(sv + " --> " + sv.getAsInetAddress());
    List<Supplier<Consumer<? extends Number>>> sup = new LinkedList();
    sup.add(()-> (Integer i)->System.out.println(i));
  }
  
  @Test
  public void host() {
    System.out.println("---- host ----");
    StringValue sv = StringValue.of("127.0.0.1:443");
    System.out.println(sv + " --> " + sv.getAsHost());
  }
  
  @Test
  public void localDate() {
    System.out.println("---- localDate ----");
    StringValue sv = StringValue.of("29/03/2020");
    System.out.println(sv + " --> " + sv.getAsLocalDate());
    sv = StringValue.of("2020-03-29");
    System.out.println(sv + " --> " + sv.getAsObject());
  }
  
  @Test
  public void localDateTime() {
    System.out.println("---- localDateTime ----");
    StringValue sv = StringValue.of("2020-03-29 18:06:20");
    System.out.println(sv + " --> " + sv.getAsLocalDateTime());
    sv = StringValue.of("2020-03-29T18:06:20");
    System.out.println(sv + " --> " + sv.getAsLocalDateTime());
  }
  
  @Test
  public void object() {
    System.out.println("---- object ----");
    StringValue sv = StringValue.of("2020-03-29 18:06:20");
    System.out.println(sv + " --> " + sv.getAsObject());
    sv = StringValue.of("TrUe");
    System.out.println(sv + " --> " + sv.getAsObject());
    sv = StringValue.of("2020,true,127.0.0.1,2020-03-29");
    System.out.println(sv + " --> " + sv.getAsObject());
  }
  
  @Test
  public void getAsClass() {
    System.out.println("---- getAsClass ----");
    StringValue sv = StringValue.of("2020-03-29 18:06:20");
    System.out.println(sv + " --> " + sv.getAs(LocalDateTime.class));
    sv = StringValue.of("TrUe");
    System.out.println(sv + " --> " + sv.getAs(Boolean.class));
    sv = StringValue.of("2020,true,127.0.0.1,2020-03-29");
    System.out.println(sv + " --> " + sv.getAs(List.class));
    sv = StringValue.of("2020");
    System.out.println(sv + " --> " + sv.getAs(Long.class));
    System.out.println(sv + " --> " + sv.getAs(Integer.class));
    System.out.println(sv + " --> " + sv.getAs(Double.class));
    sv = StringValue.of("/etc/mysql/my.cnf");
    System.out.println(sv + " --> " + sv.getAs(Path.class));
    sv = StringValue.of("192.168.0.15:8080");
    System.out.println(sv + " --> " + sv.getAs(Host.class));
    sv = StringValue.of("192.168.0.15");
    System.out.println(sv + " --> " + sv.getAs(InetAddress.class));
    sv = StringValue.of("{10,100}");
    System.out.println(sv + " --> " + sv.getAs(Range.class));
  }
  
}
