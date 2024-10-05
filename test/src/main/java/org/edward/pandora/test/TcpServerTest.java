package org.edward.pandora.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.edward.pandora.common.netty.ext.codec.decoder.FrameDecoder;
import org.edward.pandora.common.netty.ext.handler.Heartbeater;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
import org.edward.pandora.common.util.DataUtil;
import org.edward.queen.tcp.Config;
import org.edward.queen.tcp.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerTest {
    private static final Logger logger = LoggerFactory.getLogger(TcpServerTest.class);

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setPort(8090);
        Server server = new Server(config);
        server.setInitializer(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new Heartbeater(1000L))
                        .addLast(new FrameDecoder(new byte[]{0x3D}, 8))
                        .addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                if(evt instanceof Heartbeater.HeartbeatEvent) {
                                    logger.info("tick......");
                                }
                                super.userEventTriggered(ctx, evt);
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                try {
                                    if(msg instanceof ByteBuf) {
                                        ByteBuf buffer = (ByteBuf) msg;
                                        logger.info("hex: {}",
                                                DataUtil.toHexString(ByteBufUtil.getReadableBytes(buffer)));
                                    }
                                } finally {
                                    ReferenceCountUtil.release(msg, ReferenceCountUtil.refCnt(msg));
                                }
                            }
                        });
            }
        });
        server.startup();
    }
}