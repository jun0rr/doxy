/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;
import net.jun0rr.doxy.http.HttpResponse;
import us.pserver.tools.FileSizeFormatter;
import us.pserver.tools.date.DateDiff;


/**
 *
 * @author Juno
 */
public class DoxyStatsHandler implements HttpHandler {
  
  private final Map<String,AtomicLong> stats;
  
  private final Gson gson;
  
  public DoxyStatsHandler(Map<String,AtomicLong> stats) {
    this.stats = stats;
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  @Override
  public Optional<? extends HttpExchange> apply(HttpExchange x) throws Exception {
    JsonObject server = new JsonObject();
    long startup = stats.get("startup").get();
    server.addProperty("name", DoxyServer.class.getName());
    server.addProperty("startup", Instant.ofEpochMilli(startup).toString());
    DateDiff dif = new DateDiff(new Date(startup), new Date());
    server.addProperty("uptime", dif.toString());
    List<JsonObject> list = getHostList();
    long count = list.stream().mapToLong(o->o.get("requestCount").getAsLong()).sum();
    long bytes = list.stream().mapToLong(o->o.get("requestBytes").getAsLong()).sum();
    server.addProperty("totalRequestCount", count);
    server.addProperty("totalRequestBytes", bytes);
    setStats(server);
    JsonArray hosts = new JsonArray();
    list.stream().map(this::setStats).forEach(hosts::add);
    server.add("hosts", hosts);
    HttpResponse res = HttpResponse.of(HttpResponseStatus.OK, gson.toJson(server));
    res.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
    return x.withResponse(res).sendAndClose();
  }
  
  private List<JsonObject> getHostList() {
    List<JsonObject> list = new LinkedList<>();
    Predicate<String> isHost = Pattern.compile("((\\.|:{1,2})?[0-9a-fA-F]{1,4}){1,8}(:[0-9]{1,5})\\.\\w+").asPredicate();
    stats.entrySet().stream()
        .filter(e->isHost.test(e.getKey()))
        .forEachOrdered(e->{ 
          int i = e.getKey().lastIndexOf(".");
          String addr = e.getKey().substring(0, i);
          Optional<JsonObject> opt = list.stream().filter(o->o.get("address").getAsString().equals(addr)).findAny();
          JsonObject host = opt.orElseGet(JsonObject::new);
          host.addProperty(e.getKey().substring(i+1), e.getValue().get());
          if(opt.isEmpty()) {
            host.addProperty("address", addr);
            list.add(host);
          }
        });
    return list;
  }
  
  private JsonObject setStats(JsonObject obj) {
    FileSizeFormatter sf = new FileSizeFormatter();
    long startup = stats.get(String.format("%s.startup", obj.get("address").getAsString())).get();
    obj.addProperty("startup", Instant.ofEpochMilli(startup).toString());
    long uptime = (System.currentTimeMillis() - startup) / 1000;
    long bytes = obj.get("requestBytes").getAsLong();
    double count = obj.get("requestCount").getAsDouble();
    obj.addProperty("requestAvgSize", sf.format(Math.round(bytes / count)));
    obj.addProperty("requestAvgTraffic", sf.format(Math.round(bytes / uptime)).concat("/sec"));
    return obj;
  }
  
}
