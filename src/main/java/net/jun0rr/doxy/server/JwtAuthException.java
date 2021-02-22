/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server;


/**
 *
 * @author Juno
 */
public class JwtAuthException extends RuntimeException {

  public JwtAuthException(String message) {
    super(message);
  }


  public JwtAuthException(String message, Throwable cause) {
    super(message, cause);
  }


  public JwtAuthException(Throwable cause) {
    super(cause);
  }
  
}
