/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy;

import net.jun0rr.doxy.common.opt.DefaultApp;

/**
 *
 * @author juno
 */
public class DoxyApp extends DefaultApp {
  
  public static final String NAME = "Doxy";
  
  public static final String VERSION = "1.0";
  
  public static final String AUTHOR = "Juno Roesler";
  
  public static final String CONTACT = "juno.rr@gmail.com";
  
  public static final String LICENSE = "Apache License 2.0";
  
  public static final String DESCRIPTION = "Direct Tcp Proxy under Http Traffic";
  
  public DoxyApp() {
    super(NAME, VERSION, AUTHOR, CONTACT, LICENSE, DESCRIPTION, DoxyAppOptions.options(), DoxyAppOptions.rules());
  }
  
}
