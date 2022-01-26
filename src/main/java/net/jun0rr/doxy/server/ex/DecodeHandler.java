/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.server.ex;

import java.util.Optional;
import net.jun0rr.doxy.http.HttpExchange;
import net.jun0rr.doxy.http.HttpHandler;


/**
 *
 * @author Juno
 */
public class DecodeHandler implements HttpHandler {
  
  public DecodeHandler() {
    //super(HttpRoute.of("\\/decode.*", HttpMethod.GET));
  }

  @Override
  public Optional<HttpExchange> apply(HttpExchange he) throws Exception {
    throw new UnsupportedOperationException();
    /*
    if(true) throw new IllegalStateException("!!!###!!!");
    System.out.println("-> DECODE_HANDLER");
    HttpRequest req = he.request();
    StringBuilder buf = new StringBuilder();
    buf.append("VERSION: ").append(req.protocolVersion()).append("\r\n");
    buf.append("HOSTNAME: ").append(req.headers().get(HttpHeaderNames.HOST, "unknown")).append("\r\n");
    buf.append("REQUEST_URI: ").append(req.uri()).append("\r\n\r\n");

    HttpHeaders headers = req.headers();
    if (!headers.isEmpty()) {
        for (Map.Entry<String, String> h: headers) {
            CharSequence key = h.getKey();
            CharSequence value = h.getValue();
            buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
        }
        buf.append("\r\n");
    }

    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
    Map<String, List<String>> params = queryStringDecoder.parameters();
    if (!params.isEmpty()) {
        for (Map.Entry<String, List<String>> p: params.entrySet()) {
            String key = p.getKey();
            List<String> vals = p.getValue();
            for (String val : vals) {
                buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
            }
        }
        buf.append("\r\n");
    }

    ByteBuf content;
    Optional<ByteBuf> body = req.content();
    if (body.isPresent() && body.get().isReadable()) {
        buf.append("CONTENT: ");
        buf.append(body.get().toString(CharsetUtil.UTF_8));
        buf.append("\r\n");
        content = Unpooled.copiedBuffer(body.get());
    }
    else {
      content = Unpooled.EMPTY_BUFFER;
    }

    buf.append("END OF CONTENT\r\n");

    HttpResponse response = HttpResponse.of(
        req.protocolVersion(), 
        req.decoderResult().isSuccess()? OK : BAD_REQUEST, 
        req.headers(), 
        content
    );
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
    return he.send(response);
    */
  }
  
}
