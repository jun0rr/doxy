/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.tcp;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import net.jun0rr.doxy.cfg.Host;


/**
 *
 * @author Juno
 */
public interface TcpChannel {
  
  public Event event();
  
  public EventChain eventChain();
  
  public EventChain closeFuture();
  
  public EventLoopGroup group();
  
  public Channel nettyChannel();
  
  public Host localHost();
  
  public Host remoteHost();
  
}
