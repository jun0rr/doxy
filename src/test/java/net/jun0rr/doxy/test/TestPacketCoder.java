/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.Cipher;
import net.jun0rr.doxy.cfg.DefaultConfigSource;
import net.jun0rr.doxy.common.DerKeyFactory;
import net.jun0rr.doxy.common.Packet;
import net.jun0rr.doxy.common.Packet.PacketImpl;
import net.jun0rr.doxy.common.PacketCollection;
import net.jun0rr.doxy.common.PacketDecoder;
import net.jun0rr.doxy.common.PacketEncoder;
import org.junit.jupiter.api.Test;
import us.pserver.tools.Unchecked;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author Juno
 */
public class TestPacketCoder {

  //private static final PrivateKey pk = Unchecked.call(()->DerKeyFactory.loadPrivateKey(Paths.get("d:/java/doxy-pk.der")));
  //private static final PublicKey pub = Unchecked.call(()->DerKeyFactory.loadPublicKey(Paths.get("d:/java/doxy-pub.der")));
  private static final PrivateKey pk = Unchecked.call(()->DerKeyFactory.loadPrivateKey(Paths.get("/home/juno/java/doxy-pk.der")));
  private static final PublicKey pub = Unchecked.call(()->DerKeyFactory.loadPublicKey(Paths.get("/home/juno/java/doxy-pub.der")));
  private static final PacketEncoder enc = new PacketEncoder(DefaultConfigSource.DEFAULT_CRYPT_ALGORITHM, pub);
  private static final PacketDecoder dec = new PacketDecoder(DefaultConfigSource.DEFAULT_CRYPT_ALGORITHM, pk);
  
  @Test
  public void cryptDecrypt() {
    System.out.println("------ cryptDecrypt ------");
    try {
      String algo = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
      //String algo = "RSA/ECB/PKCS1Padding";
      Cipher enc = Cipher.getInstance(algo);
      enc.init(Cipher.ENCRYPT_MODE, pub);
      Cipher dec = Cipher.getInstance(algo);
      dec.init(Cipher.DECRYPT_MODE, pk);
      
      ByteBuffer data = StandardCharsets.UTF_8.encode("Hello World");
      ByteBuffer crypt = ByteBuffer.allocate(enc.getOutputSize(data.remaining()));
      
      enc.doFinal(data, crypt);
      System.out.printf("'%s' -->> '%s'%n", data, crypt);
      data.flip();
      crypt.flip();
      System.out.printf("flip: '%s' -->> '%s'%n", data, crypt);
      System.out.printf("'%s' -->> '%s'%n", StandardCharsets.UTF_8.decode(data), StandardCharsets.UTF_8.decode(crypt));
      data.flip();
      crypt.flip();
      
      data = ByteBuffer.allocate(dec.getOutputSize(crypt.remaining()));
      System.out.printf("beforeDecrypt: '%s' -->> '%s'%n", crypt, data);
      //enc.init(Cipher.DECRYPT_MODE, pk);
      dec.doFinal(crypt, data);
      crypt.flip();
      data.flip();
      System.out.printf("flip: '%s' -->> '%s'%n", crypt, data);
      System.out.printf("'%s' -->> '%s'%n", crypt, StandardCharsets.UTF_8.decode(data));
      data.flip();
      crypt.flip();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void packetEncodeDecode() throws IOException {
    System.out.println("------ packetEncodeDecode ------");
    try {
      ByteBuffer data = StandardCharsets.UTF_8.encode("Hello World");
      Packet p = new PacketImpl("channel-1", data, Host.of("localhost", 6060), 0L, data.remaining(), false);
      System.out.println(p);
      System.out.println(StandardCharsets.UTF_8.decode(p.data()));
      p.data().flip();

      p = enc.encodePacket(p);
      System.out.println(p);
      System.out.println(StandardCharsets.UTF_8.decode(p.data()));
      p.data().flip();

      p = dec.decodePacket(p);
      System.out.println(p);
      System.out.println(StandardCharsets.UTF_8.decode(p.data()));
      p.data().flip();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testPacketToFromBuffer() {
    System.out.println("------ testPacketToFromBuffer ------");
    try {
      ByteBuffer data = StandardCharsets.UTF_8.encode("Hello World");
      Packet p = new PacketImpl("channel-1", data, Host.of("172.29.14.10", 6060), 0L, data.remaining(), false);
      System.out.println(p + " --> " + StandardCharsets.UTF_8.decode(p.data()));
      p.data().flip();
      //ByteBuffer eb = enc.encode(p);
      ByteBuffer eb = p.toByteBuffer();
      System.out.println(eb);
      //p = dec.decode(eb);
      p = Packet.of(eb);
      System.out.println(p + " --> " + StandardCharsets.UTF_8.decode(p.data()));
      p.data().flip();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw Unchecked.unchecked(e);
    }
  }
  
  @Test
  public void testPacketCollection() {
    System.out.println("------ testPacketCollection ------");
    try {
      List<Packet> ls = new LinkedList<>();
      for(int i = 0; i < 10; i++) {
        ByteBuffer data = StandardCharsets.UTF_8.encode(String.format("Hello World [%d]", i));
        Host host = Host.of(String.format("172.29.14.%d", (i+1)), (6060 + i));
        Packet p = new PacketImpl(String.format("channel-%d", i), data, host, i, data.remaining(), false);
        System.out.println(p);
        ls.add(enc.encodePacket(p));
        //ls.add(p);
      }
      ls.stream()
          //.map(p->new StringBuilder(p.toString()).append(" --> ").append(StandardCharsets.UTF_8.decode(p.data()).toString()))
          .forEach(System.out::println);
      //ls.stream()
          //.peek(p->p.data().flip())
          //.forEach(System.out::println);
          //.forEach(p->p.data().flip());
      System.out.println("---------------------------------");
      PacketCollection col = new PacketCollection(ls);
      ByteBuffer bcol = col.toByteBuffer();
      System.out.println("bcol=" + bcol);
      col = PacketCollection.of(bcol);
      col.stream()
          .peek(System.out::println)
          .map(dec::decodePacket)
          .map(p->new StringBuilder(p.toString()).append(" --> ").append(StandardCharsets.UTF_8.decode(p.data()).toString()))
          .forEach(System.out::println);
      System.out.println("---------------------------------");
    }
    catch(Exception e) {
      e.printStackTrace();
      throw Unchecked.unchecked(e);
    }
  }
  
}
