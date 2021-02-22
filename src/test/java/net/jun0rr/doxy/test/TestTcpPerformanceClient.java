/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.tcp.TcpClient;
import org.junit.jupiter.api.RepeatedTest;
import us.pserver.tools.Timer;


/**
 *
 * @author Juno
 */
public class TestTcpPerformanceClient {
  /*
  private static final int TOTAL = 10000;
  
  public AtomicInteger COUNT = new AtomicInteger(TOTAL);
  
  @RepeatedTest(5)
  public void tcpClient() throws InterruptedException {
    System.out.println("----- tcpClient() -----");
    Host target = Host.of("localhost:4322");
    EventLoopGroup clients = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*8);
    Timer tm = new Timer.Nanos();
    tm.start();
    for(int i = 0; i < TOTAL; i++) {
      ByteBuf msg = Unpooled.copiedBuffer("Hello World!", StandardCharsets.UTF_8);
      TcpClient.open(clients)
          .addMessageHandler(()->x->{
            if(COUNT.getAndDecrement() % 1000 == 0) {
              System.out.println("[" + x.tcpChannel().channel().get().localAddress() + "] " + COUNT);
            }
            return x.close();
          })
          .connect(target)
          .write(msg)
          .start();
    }
    
    System.out.println("* Waiting CountDown...");
    int cc = COUNT.get();
    int retry = 80;
    while(COUNT.get() > 0) {
      if(retry <= 0) {
        System.out.println("# ERROR clients(): COUNT(" + COUNT + ") is static for " + 80*50 + " millis...");
        break;
      }
      Thread.sleep(50);
      if(COUNT.get() == cc) {
        retry--;
      }
      cc = COUNT.get();
    }
      
    tm.stop();
    System.out.println(tm);
    clients.shutdownGracefully();
  }
  */
}
