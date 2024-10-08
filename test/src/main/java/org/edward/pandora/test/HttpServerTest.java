package org.edward.pandora.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
import org.edward.pandora.common.netty.ext.server.Config;
import org.edward.pandora.common.netty.ext.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerTest.class);

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setPort(8090);
        Server server = new Server(config);
        server.setInitializer(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(1024*1024))
                        .addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
                                    throws Exception {
                                logger.info("uri: {}", msg.uri());
                                ByteBuf content = msg.content();
                                String jsonData = new String(ByteBufUtil.getReadableBytes(content));
                                logger.info("json: {}", jsonData);
                                FullHttpResponse response = new DefaultFullHttpResponse(
                                        HttpVersion.HTTP_1_1,
                                        HttpResponseStatus.OK,
                                        Unpooled.copiedBuffer("Hello world!", CharsetUtil.UTF_8));
                                response.headers()
                                        .set(HttpHeaderNames.CONTENT_TYPE, "text/plain")
                                        .set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                                ctx.writeAndFlush(response);
                            }
                        });
            }
        });
        server.startup();
    }
}