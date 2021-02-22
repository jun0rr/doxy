/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.util.function.Supplier;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.tcp.TcpHandler;
import net.jun0rr.doxy.tcp.TcpServer;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestTcpPerformanceServer {
  /*
  @Test
  public void tcpServer() throws InterruptedException {
    System.out.println("----- tcpServer() -----");
    Supplier<TcpHandler> h = ()->x->x.sendAndClose();
    TcpServer server = TcpServer.open()
        .addMessageHandler(h)
        .bind(Host.of("0.0.0.0:4322"))
        .onComplete(c->System.out.println("* Server listening: " + c.localAddress()))
        .start()
        .sync();
    server.channel().get().closeFuture().sync();
    server.shutdown().sync();
  }
  */
}
