/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 *
 * @author Juno
 */
public class DerKeyFactory {
  
  public static PrivateKey createPrivateKey(byte[] bob) throws IOException {
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bob);
      PrivateKey pk = kf.generatePrivate(spec);
      return pk;
    }
    catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IOException(e.toString(), e);
    }
  }
  
  public static PublicKey createPublicKey(byte[] bob) throws IOException {
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec spec = new X509EncodedKeySpec(bob);
      PublicKey pub = kf.generatePublic(spec);
      return pub;
    }
    catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IOException(e.toString(), e);
    }
  }
  
  public static PrivateKey loadPrivateKey(Path pkDer) throws IOException {
    return createPrivateKey(Files.readAllBytes(pkDer));
  }
  
  public static PublicKey loadPublicKey(Path pubDer) throws IOException {
    return createPublicKey(Files.readAllBytes(pubDer));
  }
  
  public static KeyPair loadKeyPair(Path pkDer, Path pubDer) throws IOException {
    return new KeyPair(loadPublicKey(pubDer), loadPrivateKey(pkDer));
  }
  
}
