/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.tcp.TcpClient;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestTcpClient {
  /*
  @Test
  public void method() {
    //TcpClient2 cli = TcpClient2.open()
    TcpClient.open()
        .addMessageHandler(()-> x->{
          //System.out.printf("--- TcpHandler: message=%s ---%n", x.message().orElse("<empty>"));
          ByteBuf msg = (ByteBuf) x.message();
          System.out.printf("[server response]>> '%s'%n", msg.toString(StandardCharsets.UTF_8));
          return x.empty();
        })
        .connect(Host.of("192.168.15.14", 2000))
        .write(Unpooled.copiedBuffer("Hello\\ World!!\n", StandardCharsets.UTF_8))
        //.closeOnComplete()
        .start()
        .close();
    //Unchecked.call(()->Thread.sleep(5000));
    //cli.close();
  }
  */
}
