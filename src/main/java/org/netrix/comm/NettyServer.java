package org.netrix.comm;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

public class NettyServer extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final NettyRouter router;

    public NettyServer(NettyRouter router) {
        this.router = router;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        HttpResponse res;
        try {
            res = this.router.handleRequest(req);
        } catch (Exception e) {
            res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.writeAndFlush(res);
    }
}
