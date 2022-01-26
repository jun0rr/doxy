/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.opt;


/**
 *
 * @author Juno
 */
public class OptionParseException extends RuntimeException {
  
  public OptionParseException() {
  }
  
  public OptionParseException(String message) {
    super(message);
  }
  
  public OptionParseException(String message, Object... args) {
    super(String.format(message, args));
  }
  
  public OptionParseException(Throwable cause, String message) {
    super(message, cause);
  }
  
  public OptionParseException(Throwable cause, String message, Object... args) {
    super(message, cause);
  }
  
}
