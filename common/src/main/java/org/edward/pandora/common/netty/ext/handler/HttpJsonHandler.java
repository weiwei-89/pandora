package org.edward.pandora.common.netty.ext.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpJsonHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpJsonHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        logger.info("uri: {}", request.uri());
        logger.info("method: {}", request.method());
        if(request.method() != HttpMethod.POST) {
            throw new Exception("only post requests are supported");
        }
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if(StringUtils.isBlank(contentType)
                || !contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString())) {
            throw new Exception("only json requests are supported");
        }
        ctx.fireChannelRead(request.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("HttpJsonHandler error", cause);
    }
}