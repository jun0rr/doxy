/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.opt.rule;

import java.util.Collection;
import net.jun0rr.doxy.opt.OptionParseException;
import net.jun0rr.doxy.opt.ParsedOption;


/**
 *
 * @author Juno - juno.rr@gmail.com
 */
public interface OptionRule {
  
  public void eval(Collection<ParsedOption> c) throws OptionParseException;
  
}
