package org.edward.pandora.common.netty.ext.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.edward.pandora.common.netty.ext.handler.Heartbeater;
import org.edward.pandora.common.netty.ext.handler.IdleHandler;
import org.edward.pandora.common.netty.ext.handler.StatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final Config config;

    public Server(Config config) {
        this.config = config;
    }

    private Channel channel;
    private ChannelInitializer<? extends SocketChannel> initializer;

    public void setInitializer(ChannelInitializer<? extends SocketChannel> initializer) {
        this.initializer = initializer;
    }

    public void startup() throws Exception {
        logger.info("starting up server [port:{}]......", this.config.getPort());
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ChannelInitializer<? extends SocketChannel> initializer = null;
            if(this.initializer == null) {
                StatusHandler statusHandler = new StatusHandler();
                initializer = new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleHandler(
                                        config.getReadTimeout(),
                                        config.getWriteTimeout(),
                                        config.getReadWriteTimeout(),
                                        TimeUnit.MILLISECONDS))
                                .addLast(statusHandler)
                                .addLast(new Heartbeater(100L));
                    }
                };
            } else {
                initializer = this.initializer;
            }
            this.channel = new ServerBootstrap()
                    .group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer)
                    .bind(this.config.getPort())
                    .sync().channel();
            logger.info("done");
            this.channel.closeFuture().sync();
            logger.info("server stopped [port:{}]", this.config.getPort());
        } finally {
            logger.info("cleaning up......");
            parentGroup.shutdownGracefully().sync();
            childGroup.shutdownGracefully().sync();
            logger.info("done");
        }
    }

    public void shutdown() throws Exception {
        logger.info("shutting down server [port:{}]......", this.config.getPort());
        if(this.channel == null) {
            logger.info("done(stopped)");
            return;
        }
        this.channel.close().sync();
        this.channel = null;
        logger.info("done");
    }
}