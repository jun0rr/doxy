/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import net.jun0rr.doxy.cfg.Host;
import net.jun0rr.doxy.tcp.TcpChannel;
import net.jun0rr.doxy.tcp.TcpHandlerSetup;
import net.jun0rr.doxy.tcp.TcpClient;
import net.jun0rr.doxy.tcp.TcpHandler;
import net.jun0rr.doxy.tcp.TcpServer;
import org.junit.jupiter.api.Test;
import net.jun0rr.doxy.tcp.ChannelHandlerSetup;
import net.jun0rr.doxy.tcp.SSLHandlerFactory;
import org.junit.jupiter.api.Assertions;


/**
 *
 * @author Juno
 */
public class TestTcpServer {
  
  @Test
  public void echoServerClient() throws URISyntaxException {
    System.out.println("------ echoServerClient ------");
    Host host = Host.of("0.0.0.0", 4455);
    Path kspath = Paths.get(getClass().getClassLoader().getResource("doxy.jks").toURI());
    SSLHandlerFactory ssl = SSLHandlerFactory.forServer(kspath, "32132155".toCharArray());
    String message = Instant.now().toString();
    ChannelHandlerSetup<TcpHandler> setup = TcpHandlerSetup.newSetup()
        .enableSSL(ssl)
        .addConnectHandler(()-> x->{
          System.out.println("[SERVER] Client connected: " + x.channel().remoteHost());
        })
        .addInputHandler(()-> x->{
          return x.withMessage(x.<ByteBuf>message().toString(StandardCharsets.UTF_8)).forward();
        })
        .addInputHandler(()-> x->{
          System.out.println("[SERVER] Received: " + x.message());
          Assertions.assertEquals(message, x.message());
          return x.forward();
        })
        .addInputHandler(()-> x->{
          return x.sendAndClose();
        })
        .addOutputHandler(()-> x->{
          System.out.println("[SERVER] Output1: " + x.message());
          return x.withMessage(Unpooled.copiedBuffer(x.<String>message(), StandardCharsets.UTF_8)).forward();
        })
        .addOutputHandler(()-> x->{
          System.out.println("[SERVER] Output2: " + x.message());
          return x.forward();
        });
    TcpChannel server = TcpServer.open(setup)
        .bind(host)
        .onComplete(c->System.out.println("[SERVER] listening on " + c.channel().localHost()))
        .executeSync()
        .context().channel();
    setup = TcpHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addConnectHandler(()-> x->{
          System.out.println("[CLIENT] ConnectHandler Connected to: " + x.channel().remoteHost());
        })
        .addInputHandler(()-> x->{
          return x.withMessage(x.<ByteBuf>message().toString(StandardCharsets.UTF_8)).forward();
        })
        .addInputHandler(()-> x->{
          System.out.println("[CLIENT] Received: " + x.message());
          Assertions.assertEquals(message, x.message());
          return x.forward();
        })
        .addInputHandler(()-> x->{
          x.channel().events()
              .shutdown()
              .onComplete(c->System.out.println("[CLIENT] Shutdown completed!"))
              .execute();
          return x.empty();
        })
        .addOutputHandler(()-> x->{
          System.out.println("[CLIENT] Output1: " + x.message());
          return x.withMessage(Unpooled.copiedBuffer(x.<String>message(), StandardCharsets.UTF_8)).forward();
        })
        .addOutputHandler(()-> x->{
          System.out.println("[CLIENT] Output2: " + x.message());
          return x.forward();
        });
    TcpClient.open(setup)
        .connect(host)
        .onComplete(c->System.out.println("[CLIENT] Connected to: " + c.channel().remoteHost()))
        //.write(Unpooled.copiedBuffer(message, StandardCharsets.UTF_8))
        .write(message)
        .onComplete(c->System.out.println("[CLIENT] Message sent!"))
        .execute()
        .awaitShutdown();
    server.events()
        .shutdown()
        .onComplete(c->System.out.println("[SERVER] Shutdown completed!"))
        .execute()
        .awaitShutdown();
  }
  
  @Test
  public void timestampServerClient() throws InterruptedException, URISyntaxException {
    System.out.println("------ timestampServerClient ------");
    Host host = Host.of("localhost", 1212);
    Path kspath = Paths.get(getClass().getClassLoader().getResource("doxy.jks").toURI());
    SSLHandlerFactory ssl = SSLHandlerFactory.forServer(kspath, "32132155".toCharArray());
    String message = Instant.now().toString();
    ChannelHandlerSetup setup = TcpHandlerSetup.newSetup()
        .enableSSL(ssl)
        .addConnectHandler(()-> x->{
          System.out.println("[SERVER] Client connected: " + x.channel().remoteHost());
          ByteBuf msg = Unpooled.copiedBuffer(message, StandardCharsets.UTF_8);
          x.channel().events().write(msg)
              .onComplete(c->System.out.println("[SERVER] Message writed!"))
              .onComplete(c->x.bootstrapChannel().events().shutdown()
                  .onComplete(cc->System.out.println("[SERVER] Shutdown complete!"))
                  .execute())
              .execute();
        });
    TcpChannel server = TcpServer.open(setup)
        .bind(host)
        .onComplete(c->System.out.println("[SERVER] Listening on " + c.channel().localHost()))
        .executeSync()
        .context().channel();
    setup = TcpHandlerSetup.newSetup()
        .enableSSL(SSLHandlerFactory.forClient())
        .addInputHandler(()-> x->{
          return x.withMessage(x.<ByteBuf>message().toString(StandardCharsets.UTF_8)).forward();
        })
        .addInputHandler(()-> x->{
          System.out.println("[CLIENT] Received: " + x.message());
          Assertions.assertEquals(message, x.message());
          return x.forward();
        })
        .addInputHandler(()-> x->{
          x.bootstrapChannel().events()
              .shutdown()
              .onComplete(c->System.out.println("[CLIENT] Shutdown completed!"))
              .execute();
          return x.empty();
        });
    TcpClient.open(setup)
        .connect(host)
        .execute()
        .awaitShutdown();
    server.events().awaitShutdown();
  }
  
  //@Test
  public void timestampServer() throws InterruptedException, URISyntaxException {
    System.out.println("------ timestampServer ------");
    try {
      Host host = Host.of("localhost", 1212);
      Path kspath = Paths.get(getClass().getClassLoader().getResource("doxy.jks").toURI());
      SSLHandlerFactory ssl = SSLHandlerFactory.forServer(kspath, "32132155".toCharArray());
      String message = Instant.now().toString();
      ChannelHandlerSetup setup = TcpHandlerSetup.newSetup()
          //.enableSSL(ssl)
          .addConnectHandler(()-> x->{
            System.out.println("[SERVER] Client connected: " + x.channel().remoteHost());
          })
          .addOutputHandler(()-> x->{
            return x.withMessage(Unpooled.copiedBuffer(x.<String>message(), StandardCharsets.UTF_8)).sendAndClose();
          });
      TcpServer.open(setup)
          .bind(host)
          .onComplete(c->System.out.println("[SERVER] Listening on " + c.channel().localHost()))
          .write(message)
          .onComplete(c->System.out.println("[SERVER] Message sent " + c.channel().remoteHost()))
          //.onComplete(c->c.channel().events().close().execute())
          .executeSync()
          .awaitShutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
