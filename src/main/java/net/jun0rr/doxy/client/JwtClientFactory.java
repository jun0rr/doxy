/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.client;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.impl.DoxyClaims;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.jose4j.zip.CompressionAlgorithmIdentifiers;


/**
 *
 * @author juno
 */
public class JwtClientFactory {
  
  private final DoxyEnvironment env;
  
  private final JsonWebSignature jws;
  
  private final JsonWebEncryption jwe;
  
  public JwtClientFactory(DoxyEnvironment env) {
    this.env = Objects.requireNonNull(env, "Bad null DoxyEnvironment");
    this.jws = new JsonWebSignature();
    jws.setPayloadCharEncoding(StandardCharsets.UTF_8.name());
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    jws.setKey(env.getPrivateKey());
    this.jwe = new JsonWebEncryption();
    jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);
    jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
    jwe.setCompressionAlgorithmHeaderParameter(CompressionAlgorithmIdentifiers.DEFLATE);
    jwe.setContentTypeHeaderValue("JWT");
    jwe.setKey(env.getPublicKey());
  }
  
  public JwtClaims createClaims() {
    JwtClaims jc = new JwtClaims();
    jc.setAudience(DoxyClaims.AUDIENCE_SERVER);
    jc.setIssuer(DoxyClaims.ISSUER_CLIENT);
    //jc.setExpirationTimeMinutesInTheFuture(DoxyClaims.EXPIRATION_MINUTES);
    jc.setSubject(DoxyClaims.SUBJECT_AUTH);
    jc.setIssuedAtToNow();
    return jc;
  }
  
  public String signPayload(String payload) throws JoseException {
    jws.setPayload(payload);
    return jws.getCompactSerialization();
  }
  
  public String encryptPayload(String payload) throws JoseException {
    jwe.setPayload(payload);
    return jwe.getCompactSerialization();
  }
  
  public String createAuthToken() throws JoseException {
    return encryptPayload(signPayload(createClaims().toJson()));
  }
  
}
