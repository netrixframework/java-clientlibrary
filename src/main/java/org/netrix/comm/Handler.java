package org.netrix.comm;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface Handler {
    HttpResponse handle(FullHttpRequest request);
}
