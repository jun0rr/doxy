/*
 * Direitos Autorais Reservados (c) 2011 Juno Roesler
 * Contato: juno.rr@gmail.com
 * 
 * Esta biblioteca é software livre; você pode redistribuí-la e/ou modificá-la sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation; tanto a versão 2.1 da Licença, ou qualquer
 * versão posterior.
 * 
 * Esta biblioteca é distribuída na expectativa de que seja útil, porém, SEM
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE
 * OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública
 * Geral Menor do GNU para mais detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto
 * com esta biblioteca; se não, acesse 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html, 
 * ou escreva para a Free Software Foundation, Inc., no
 * endereço 59 Temple Street, Suite 330, Boston, MA 02111-1307 USA.
 */

package net.jun0rr.doxy.opt;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 14/12/2016
 */
public class StringPad {
  
  private final String str;
  
  
  private StringPad(String str) {
    Objects.requireNonNull(str, "Bad Null String");
    this.str = str;
  }
  
  
  public StringPad with(String str) {
    return new StringPad(str);
  }
  
  
  public static StringPad of(String str) {
    return new StringPad(str);
  }
  

  public String lpad(String pad, int length) {
    Objects.requireNonNull(pad, "Bad Null String Pad");
    if(length <= str.length()) return str;
    StringBuilder sb = new StringBuilder(str);
    while(sb.length() < length) {
      sb.insert(0, pad);
    }
    if(length <= sb.length()) {
      sb.delete(0, (sb.length() - length));
    }
    return sb.toString();
  }
  

  public String rpad(String pad, int length) {
    Objects.requireNonNull(pad, "Bad Null String Pad");
    if(length <= str.length()) return str;
    StringBuilder sb = new StringBuilder(str);
    while(sb.length() < length) {
      sb.append(pad);
    }
    if(length <= sb.length()) {
      sb.delete(length, sb.length());
    }
    return sb.toString();
  }
  

  public String cpad(String pad, int length) {
    Objects.requireNonNull(pad, "Bad Null String Pad");
    if(length <= str.length()) return str;
    StringBuilder sb = new StringBuilder();
    while((sb.length() + str.length()) < length) {
      sb.append(pad);
    }
    sb.insert((sb.length() / 2), str);
    boolean tail = true;
    while(sb.length() > length) {
      if(tail) sb.deleteCharAt(sb.length() -1);
      else sb.deleteCharAt(0);
      tail = !tail;
    }
    return sb.toString();
  }
  
  
  public List<String> centerLines(String sep, int length) {
    Objects.requireNonNull(sep, "Bad Null String Separator");
    if(str.length() <= length) return List.of(rpad(sep, length));
    List<String> lns = new LinkedList<>();
    List<String> words = List.of(str.split(sep));
    List<String> ln = new LinkedList<>();
    StringBuilder ll = new StringBuilder();
    for(String w : words) {
      if(ll.length() + sep.length() + w.length() < length) {
        ll.append(w).append(sep);
      }
      else {
        if(ll.length() == 0) {
          lns.add(w);
        }
        else {
          lns.add(of(ll.substring(0, ll.length() - sep.length())).cpad(" ", length));
        }
        ll.delete(0, ll.length());
        ll.append(w).append(sep);
      }
    }
    if(ll.length() > 0) {
      lns.add(of(ll.substring(0, ll.length() - sep.length())).cpad(" ", length));
    }
    return lns;
  }
  
  
  public List<String> justifyLines(String sep, int length) {
    Objects.requireNonNull(sep, "Bad Null String Separator");
    if(str.length() <= length) return List.of(rpad(sep, length));
    List<String> lns = new LinkedList<>();
    List<String> words = List.of(str.split(sep));
    List<String> ln = new LinkedList<>();
    for(String w : words) {
      if(ln.stream().mapToInt(String::length).sum() + sep.length() * ln.size() + w.length() < length) {
        ln.add(w);
      }
      else {
        if(ln.isEmpty()) {
          lns.add(w);
        }
        else if(ln.size() > 1) {
          lns.add(of(ln.get(0)).concat(" ", length, ln.subList(1, ln.size())));
        }
        else {
          lns.add(of(ln.get(0)).rpad(" ", length));
        }
        ln.clear();
        ln.add(w);
      }
    }
    if(ln.size() > 1) {
      lns.add(of(ln.get(0)).concat(" ", length, ln.subList(1, ln.size())));
    }
    else if(!ln.isEmpty()) {
      lns.add(of(ln.get(0)).rpad(" ", length));
    }
    return lns;
  }
  
  
  public String justify(String sep, int length) {
    List<String> lns = justifyLines(sep, length);
    StringBuilder sb = new StringBuilder();
    lns.forEach(l->sb.append(l).append("\n"));
    return sb.toString();
  }
  
  
  public String concat(String sep, int length, String ... args) {
    return concat(sep, length, List.of(args));
  }
  
  public String concat(String sep, int length, List<String> args) {
    Objects.requireNonNull(sep, "Bad Null String Separator");
    Objects.requireNonNull(args, "Bad Null String Arguments");
    if(sep.length() == 0 || args.isEmpty()) {
      throw new IllegalArgumentException("Bad String Length: '"+ sep+ "', "+ args);
    }
    int[] iargs = new int[args.size()];
    StringBuilder sb = new StringBuilder(str);
    iargs[0] = sb.length();
    for(int i = 0; i < args.size(); i++) {
      sb.append(args.get(i));
      if(i < iargs.length -1) {
        iargs[i+1] = sb.length();
      }
    }
    int argslen = args.stream().map(String::length).reduce(0, Integer::sum) + str.length();
    if(length < argslen) {
      return sb.toString();
    }
    while(sb.length() < length) {
      for(int i = 0; i < args.size(); i++) {
        sb.insert(iargs[i], sep);
        for(int j = i+1; j < iargs.length; j++) {
          iargs[j] += sep.length();
        }
      }
    }
    int i = iargs.length -1;
    while(sb.length() > length) {
      sb.deleteCharAt(iargs[i]);
      iargs[i] -= 1;
      if(i-- < 0) i = iargs.length;
    }
    return sb.toString();
  }
  
}
