package org.netrix.comm;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.netrix.Event;
import org.netrix.timeouts.Timeout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;

public class MessageHandler implements Handler{

    private Vector<Message> messages;
    private NetrixClient client;

    public MessageHandler(NetrixClient client) {
        this.messages = new Vector<Message>();
        this.client = client;
    }

    @Override
    public HttpResponse handle(FullHttpRequest req) {
        try {
            Message m = getMessageFromReq(req);
            messages.add(m);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("message_id", m.getId());

            client.sendEvent(new Event(
                    "MessageReceive",
                    params
            ));

            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK
            );
        } catch (Exception e) {

        }
        return null;
    }

    private Message getMessageFromReq(FullHttpRequest req) throws IOException {
        ByteBuf content = req.content();
        if(content == null || content.readableBytes() <= 0){
            throw new IOException("empty request");
        }
        if(!req.headers().get(CONTENT_TYPE).equals(APPLICATION_JSON.toString())) {
            throw new IOException("not a json request");
        }
        return Message.fromJsonString(content.toString(CharsetUtil.UTF_8));
    }

    public Vector<Message> getMessages() {
        Vector<Message> result = new Vector<Message>();
        result.addAll(messages);
        messages.clear();
        return result;
    }
}
