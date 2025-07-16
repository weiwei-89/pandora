package org.edward.pandora.common.netty.ext.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.edward.pandora.common.model.Response;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
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
        if(StringUtils.isBlank(contentType) || !contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString())) {
            throw new Exception("only json requests are supported");
        }
        ByteBuf content = request.content();
        String jsonData = new String(ByteBufUtil.getReadableBytes(content));
        logger.info("json: {}", jsonData);
        Response response = new Response(200, "ok");
        response.setData("Hello world!");
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(JSON.toJSONString(response), CharsetUtil.UTF_8)
        );
        httpResponse.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes())
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Response response = new Response(500, "error");
        response.setData(cause.getMessage());
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(JSON.toJSONString(response), CharsetUtil.UTF_8)
        );
        httpResponse.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes())
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }
}