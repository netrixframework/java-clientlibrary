package io.github.netrixframework.comm;

import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.github.netrixframework.DirectiveExecutor;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;

public class DirectiveHandler implements Handler{

    DirectiveExecutor executor;

    public DirectiveHandler(DirectiveExecutor executor) {
        this.executor = executor;
    }
    @Override
    public HttpResponse handle(FullHttpRequest req) {
        ByteBuf content = req.content();
        if(content == null || content.readableBytes() <= 0) {
            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST
            );
        }
        if(!req.headers().get(CONTENT_TYPE).equals(APPLICATION_JSON.toString())) {
            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST
            );
        }
        try {
            String directiveJson = content.toString(CharsetUtil.UTF_8);
            String action = JsonParser.parseString(directiveJson).getAsJsonObject().get("action").getAsString();

            switch (action) {
                case "START":
                    executor.start();
                    break;
                case "STOP":
                    executor.stop();
                    break;
                case "RESTART":
                    executor.restart();
                    break;
            }
            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK
            );
        } catch (Exception e) {

        }
        return null;
    }
}
