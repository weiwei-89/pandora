package org.edward.pandora.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.cli.*;
import org.edward.pandora.common.netty.ext.codec.decoder.FrameDecoder;
import org.edward.pandora.common.netty.ext.handler.Heartbeater;
import org.edward.pandora.common.netty.ext.handler.IdleHandler;
import org.edward.pandora.common.netty.ext.handler.StatusHandler;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
import org.edward.pandora.common.util.DataUtil;
import org.edward.pandora.common.netty.ext.server.Config;
import org.edward.pandora.common.netty.ext.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TcpServerTest {
    private static final Logger logger = LoggerFactory.getLogger(TcpServerTest.class);
    private static final String LISTEN_PORT = "listen.port";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder().longOpt(LISTEN_PORT).required(true).hasArg(true).build());
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        int listenPort = Integer.parseInt(cmd.getOptionValue(LISTEN_PORT));
        Config config = new Config();
        config.setPort(listenPort);
        StatusHandler statusHandler = new StatusHandler();
        Server server = new Server(config);
        server.setInitializer(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new IdleHandler(
                                10000L,
                                0,
                                0,
                                TimeUnit.MILLISECONDS)
                        )
                        .addLast(statusHandler)
                        .addLast(new Heartbeater(100L))
//                        .addLast(new FrameDecoder(new byte[]{0x3D}, 8))
                        .addLast(new LineBasedFrameDecoder(512))
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