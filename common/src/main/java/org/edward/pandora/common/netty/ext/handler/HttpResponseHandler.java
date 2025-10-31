package org.edward.pandora.common.netty.ext.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.edward.pandora.common.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseHandler.class);
    private final ObjectMapper objectMapper;

    public HttpResponseHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("HttpResponseHandler added");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = Response.ok().setData(msg);
        FullHttpResponse httpResponse = this.buildHttpResponse(response);
        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("HttpResponseHandler error", cause);
        Response response = Response.error(cause.getMessage());
        FullHttpResponse httpResponse = this.buildHttpResponse(response);
        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    private FullHttpResponse buildHttpResponse(Response response) throws Exception {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(this.objectMapper.writeValueAsString(response), CharsetUtil.UTF_8)
        );
        httpResponse.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes())
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        return httpResponse;
    }
}