package org.edward.pandora.common.netty.ext.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import org.edward.pandora.common.http.ApiLoader;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpDispatchHandler extends MessageToMessageDecoder<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpDispatchHandler.class);

    private final ApiLoader apiLoader;

    public HttpDispatchHandler(ApiLoader apiLoader) {
        this.apiLoader = apiLoader;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("HttpDispatchHandler added");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest request, List<Object> out) throws Exception {
        String uri = request.uri();
        ByteBuf content = request.content();
        String json = new String(ByteBufUtil.getReadableBytes(content));
        logger.info("json: {}", json);
        Object result = this.apiLoader.execute(uri, json);
        logger.info("result: {}", JSON.toJSONString(result));
        if(result == null) {
            out.add("");
        } else {
            out.add(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("HttpDispatchHandler error", cause);
    }
}