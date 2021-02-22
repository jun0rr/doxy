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

package net.jun0rr.doxy.server.http.util;

import net.jun0rr.doxy.common.StringValue;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import us.pserver.tools.Indexed;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 03/04/2017
 */
public class RequestParam {
  
  private final Map<String,List<String>> pars;
  
  public RequestParam(Map<String,List<String>> pars) {
    if(pars == null) {
      throw new IllegalArgumentException("Bad null parameters map");
    }
    this.pars = pars;
  }
  
  public Map<String,List<String>> getParameters() {
    return this.pars;
  }
  
  public boolean containsValid(String key) {
    String val = pars.get(key).get(0);
    return val != null && !val.isBlank();
  }
  
  public boolean contains(String key) {
    return pars.containsKey(key);
  }
  
  public int size() {
    return pars.size();
  }
  
  public boolean isEmpty() {
    return pars.isEmpty();
  }
  
  public StringValue get(String key) {
    return StringValue.of(pars.get(key).get(0));
  }
  
  public List<StringValue> getAsList(String key) {
    List<String> l = pars.get(key);
    if(l == null || l.isEmpty()) return Collections.EMPTY_LIST;
    return l.stream().map(StringValue::of).collect(Collectors.toList());
  }
  
  @Override
  public String toString() {
    return pars.toString();
  }
  
  public static RequestParam fromUriQueryString(String uri) {
    Objects.requireNonNull(uri, "Bad null URI");
    QueryStringDecoder dec = new QueryStringDecoder(uri);
    return new RequestParam(dec.parameters());
  }
  
  public static RequestParam fromUriPattern(String pattern, String uri) {
    String[] ptk = pattern.split("/");
    UriParam pars = new UriParam(uri);
    return new RequestParam(List.of(ptk).stream()
        .filter(s->!s.isBlank())
        .map(Indexed.builder())
        .peek(System.out::println)
        .filter(s->s.value().matches("\\{\\w+\\}"))
        .collect(Collectors.toMap(i->i.value().substring(1, i.value().length()-1), i->List.of(pars.get(i.index()).toString())))
    );
  }
  
}
