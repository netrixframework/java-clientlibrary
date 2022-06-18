package org.netrix.comm;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.netrix.Event;
import org.netrix.timeouts.Timeout;
import org.netrix.timeouts.Timer;

import java.io.IOException;
import java.util.HashMap;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;

public class TimeoutHandler implements Handler {

    private Timer timer;
    private NetrixClient client;

    public TimeoutHandler(Timer timer, NetrixClient client) {
        this.timer = timer;
        this.client = client;
    }
    @Override
    public HttpResponse handle(FullHttpRequest req) {
        try {
            Timeout t = getTimeoutFromReq(req);
            timer.fireTimeout(t.key());

            long duration = Math.max(t.getDuration().toMillis(),0);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("type", t.key());
            params.put("duration", String.format("%dms", duration));

            client.sendEvent(new Event(
                    "TimeoutEnd",
                    params
            ));

            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK
            );
        } catch (Exception e){
        }
        return null;
    }

    private Timeout getTimeoutFromReq(FullHttpRequest req) throws IOException {
        ByteBuf content = req.content();
        if(content == null || content.readableBytes() <= 0){
            throw new IOException("empty request");
        }
        if(!req.headers().get(CONTENT_TYPE).equals(APPLICATION_JSON.toString())) {
            throw new IOException("not a json request");
        }
        return Timeout.fromJsonString(content.toString(CharsetUtil.UTF_8));
    }
}
