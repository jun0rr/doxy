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

package net.jun0rr.doxy.http.util;

import net.jun0rr.doxy.common.StringValue;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 22/07/2016
 */
public class UriParam {
  
  private final String uri;
  
  private final List<StringValue> params;
  
  public UriParam(String uri) {
    this.uri = uri;
    this.params = List.of(uri.startsWith("/") 
        ? this.uri.substring(1).split("/") 
        : this.uri.split("/")).stream()
        .map(StringValue::of)
        .collect(Collectors.toList());
  }
  
  public int length() {
    return params.size() -1;
  }
  
  public String getUri() {
    return uri;
  }
  
  public String getContext() {
    return params.get(0).toString();
  }
  
  public StringValue get(int index) {
    return params.get(index);
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.uri);
    return hash;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final UriParam other = (UriParam) obj;
    if (!Objects.equals(this.uri, other.uri)) {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString() {
    return "URIParam{" + "uri=" + uri + ", params=" + params + '}';
  }
  
}
