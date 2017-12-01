package org.ib.eval.netty.http.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.netty.buffer.Unpooled.copiedBuffer;


public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LogManager.getLogger(HttpRequestHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        logger.debug("Request received: {}", request.uri());

        final String responseMessage = "Hello from Netty: " + request.uri();

        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            copiedBuffer(responseMessage.getBytes())
        );

        if (HttpUtil.isKeepAlive(request))
        {
            response.headers().set(
                HttpHeaderNames.CONNECTION,
                HttpHeaderValues.KEEP_ALIVE
            );
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseMessage.length());

        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


// https://logging.apache.org/log4j/2.x/manual/configuration.html