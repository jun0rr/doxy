/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import net.jun0rr.doxy.common.opt.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestOption {
  
  @Test
  public void option_equals_name_or_alias() {
    Option port = Option.of("-p")
        .alias("--port")
        .description("Port that service will bind to")
        .acceptArgument(true)
        .setRepeatable(false)
        .get();
    Assertions.assertEquals(port, Option.of("-p").get());
    Assertions.assertEquals(port, Option.of("--port").get());
  }
  
  @Test
  public void option_to_string() {
    Option port = Option.of("-p")
        .alias("--port")
        .description("Port that service will bind to")
        .acceptArgument(true)
        .setRepeatable(false)
        .get();
    Assertions.assertEquals(port.toString(), "-p/--port");
  }
  
}
