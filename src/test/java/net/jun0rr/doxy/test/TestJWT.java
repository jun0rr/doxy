/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.jose4j.zip.CompressionAlgorithmIdentifiers;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Juno
 */
public class TestJWT {
  
  public PrivateKey loadPrivateKey(Path pkPem) throws IOException {
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Files.readAllBytes(pkPem));
      PrivateKey pk = kf.generatePrivate(spec);
      System.out.println("* pk.getPrivateExponent(): " + ((RSAPrivateKey)pk).getPrivateExponent());
      System.out.println("* pk.getModulus(): " + ((RSAPrivateKey)pk).getModulus());
      return pk;
    }
    catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IOException(e.toString(), e);
    }
  }
  
  public PublicKey loadPublicKey(Path pubPem) throws IOException {
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec spec = new X509EncodedKeySpec(Files.readAllBytes(pubPem));
      PublicKey pk = kf.generatePublic(spec);
      System.out.println("* pk.getPublicExponent(): " + ((RSAPublicKey)pk).getPublicExponent());
      System.out.println("* pk.getModulus(): " + ((RSAPublicKey)pk).getModulus());
      return pk;
    }
    catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IOException(e.toString(), e);
    }
  }
  
  //@Test
  public JwtClaims createJwtClaims() {
    JwtClaims jc = new JwtClaims();
    jc.setAudience("doxy-server");
    jc.setIssuer("doxy-client");
    jc.setExpirationTimeMinutesInTheFuture(30f);
    jc.setSubject("channel-auth");
    jc.setIssuedAtToNow();
    jc.setClaim("name", "Juno");
    jc.setClaim("email", "juno.rr@gmail.com");
    jc.setClaim("channel-id", "123456");
    jc.setClaim("target-addr", "localhost:443");
    return jc;
  }
  
  //@Test
  public String signJwt() throws JoseException, IOException {
    JsonWebSignature jws = new JsonWebSignature();
    jws.setPayload(createJwtClaims().toJson());
    jws.setPayloadCharEncoding(StandardCharsets.UTF_8.name());
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    jws.setKey(loadPrivateKey(Paths.get("d:/java/doxy-pk.der")));
    jws.sign();
    return jws.getCompactSerialization();
  }
  
  public String encryptJwt() throws JoseException, IOException {
    JsonWebEncryption jwe = new JsonWebEncryption();
    jwe.setPayload(signJwt());
    jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);
    jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
    jwe.setCompressionAlgorithmHeaderParameter(CompressionAlgorithmIdentifiers.DEFLATE);
    jwe.setContentTypeHeaderValue("JWT");
    jwe.setKey(loadPublicKey(Paths.get("d:/java/doxy-pub.der")));
    return jwe.getCompactSerialization();
  }
  
  @Test
  public void showJwsJwt() throws JoseException, IOException {
    System.out.println("* showJwsJwt: " + signJwt());
  }
  
  @Test
  public void showJweJwsJwt() throws JoseException, IOException {
    System.out.println("* showJweJwsJwt: " + encryptJwt());
  }
  
  @Test
  public void validateSign() throws IOException, JoseException {
    JsonWebSignature jws = new JsonWebSignature();
    jws.setAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256));
    jws.setCompactSerialization(signJwt());
    jws.setKey(loadPublicKey(Paths.get("d:/java/doxy-pub.der")));
    System.out.println("* jws.verifySignature(): " + jws.verifySignature());
  }
  
  @Test
  public void validateJwtClaims() throws IOException, JoseException {
    JwtConsumer cs = new JwtConsumerBuilder()
        .setJweAlgorithmConstraints(ConstraintType.WHITELIST, KeyManagementAlgorithmIdentifiers.RSA_OAEP_256)
        .setJweContentEncryptionAlgorithmConstraints(ConstraintType.WHITELIST, ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512)
        .setJwsAlgorithmConstraints(ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256)
        .setVerificationKey(loadPublicKey(Paths.get("d:/java/doxy-pub.der")))
        .setDecryptionKey(loadPrivateKey(Paths.get("d:/java/doxy-pk.der")))
        .setRequireExpirationTime()
        .setRequireIssuedAt()
        .setExpectedIssuer("doxy-client")
        .setExpectedSubject("channel-auth")
        .setExpectedAudience("doxy-server")
        .build();
    try {
      JwtClaims jc = cs.processToClaims(encryptJwt());
      System.out.println(jc);
    }
    catch(InvalidJwtException e) {
      e.getErrorDetails().stream().forEach(System.out::println);
      e.printStackTrace();
    }
  }
  
}
