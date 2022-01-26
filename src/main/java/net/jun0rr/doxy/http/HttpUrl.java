/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.http;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author juno
 */
public class HttpUrl {
  
  public static final Pattern URL_PATTERN = Pattern.compile("(http[s]?)://([a-zA-Z0-9.]+):?([0-9]{2,5})?(/.*)?");
  
  private final String url;
  
  private final Matcher matcher;
  
  public HttpUrl(String url) {
    this.url = Objects.requireNonNull(url);
    this.matcher = URL_PATTERN.matcher(url);
    if(!matcher.find()) {
      throw new IllegalArgumentException("Bad URL: " + url);
    }
  }
  
  public static HttpUrl of(String url) {
    return new HttpUrl(url);
  }
  
  public static boolean isUrl(String url) {
    return URL_PATTERN.asPredicate().test(url);
  }
  
  public String getProtocol() {
    return matcher.groupCount() >= 1 ? matcher.group(1) : null;
  }
  
  public String getHost() {
    return matcher.groupCount() >= 2 ? matcher.group(2) : null;
  }
  
  public int getPort() {
    if(matcher.groupCount() >= 3 && matcher.group(3) != null) {
      return Integer.parseInt(matcher.group(3));
    }
    else if(getProtocol() != null && getProtocol().equalsIgnoreCase("https")) {
      return 443;
    }
    else return 80;
  }
  
  public String getURI() {
    return (matcher.find(0) 
        && matcher.groupCount() >= 4 
        && matcher.group(4) != null) 
        ? matcher.group(4) : "/";
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.url);
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
    final HttpUrl other = (HttpUrl) obj;
    if (!Objects.equals(this.url, other.url)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return url;
  }
  
}
