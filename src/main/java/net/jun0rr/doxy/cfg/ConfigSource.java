/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.cfg;


/**
 *
 * @author juno
 */
public interface ConfigSource extends Comparable<ConfigSource> {
  
  public DoxyConfigBuilder load() throws Exception;
  
  public int weight();
  
  @Override
  public default int compareTo(ConfigSource c) {
    return Integer.compare(weight(), c.weight());
  }
  
}
