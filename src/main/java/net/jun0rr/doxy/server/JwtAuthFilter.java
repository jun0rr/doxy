/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Objects;
import java.util.Optional;
import net.jun0rr.doxy.common.DoxyEnvironment;
import net.jun0rr.doxy.http.HttpExceptionalResponse;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;


/**
 *
 * @author Juno
 */
public class JwtAuthFilter implements HttpHandler {
  
  public static final String JWTCLAIMS = "jwtclaims";
  
  public static final String EXPECTED_ISSUER = "doxy-client";
  
  public static final String EXPECTED_AUDIENCE = "doxy-server";
  
  public static final String EXPECTED_SUBJECT = "channel-auth";
  
  /**
   * In Authorization header, JWT token starts after 'Bearer '. (Authorization: Bearer <token>)
   */
  public static final int AUTH_TOKEN_IDX = 7;
  
  private final DoxyEnvironment env;
  
  private final JwtConsumer consumer;
  
  public JwtAuthFilter(DoxyEnvironment env) {
    this.env = Objects.requireNonNull(env, "Bad null DoxyEnvironment");
    this.consumer = new JwtConsumerBuilder()
        .setJweAlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, KeyManagementAlgorithmIdentifiers.RSA_OAEP_256)
        .setJweContentEncryptionAlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512)
        .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256)
        .setVerificationKey(env.getPublicKey())
        .setDecryptionKey(env.getPrivateKey())
        //.setRequireExpirationTime()
        .setRequireIssuedAt()
        .setExpectedIssuer(EXPECTED_ISSUER)
        .setExpectedAudience(EXPECTED_AUDIENCE)
        .setExpectedSubject(EXPECTED_SUBJECT)
        .build();
  }

  @Override
  public Optional<HttpExchange> apply(HttpExchange he) throws Exception {
    if(he.isHttpMessage()) {
      printRequest(he.request());
      if(!he.request().headers().contains(HttpHeaderNames.AUTHORIZATION)) {
        return he.response(HttpExceptionalResponse.of(
            HttpResponseStatus.UNAUTHORIZED, 
            new JwtAuthException("Authorization header missing"))
        ).sendAndClose();
      }
      try {
        String auth = he.request().headers().get(HttpHeaderNames.AUTHORIZATION);
        JwtClaims jc = consumer.processToClaims(auth.substring(AUTH_TOKEN_IDX));
        he.session().put(JWTCLAIMS, jc);
        return he.forward();
      }
      catch(InvalidJwtException e) {
        e.printStackTrace();
        return he.response(HttpExceptionalResponse.of(HttpResponseStatus.UNAUTHORIZED, e))
            .sendAndClose();
      }
    }
    return he.forward();
  }
  
  private void printRequest(HttpRequest req) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(req.method()).append(" ").append(req.uri()).append("]\n");
    req.headers().forEach(e->sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
    System.out.println(sb);
  }
  
  private void printResponse(HttpResponse res) {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(res.status()).append("]\n");
    res.headers().forEach(e->sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
    System.out.println(sb);
  }
  
}
