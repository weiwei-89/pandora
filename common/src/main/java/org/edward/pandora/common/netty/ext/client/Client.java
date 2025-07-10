package org.edward.pandora.common.netty.ext.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.edward.pandora.common.netty.ext.codec.encoder.Appender;
import org.edward.pandora.common.netty.ext.handler.StatusHandler;

public class Client {
    private Client() {

    }

    public static Client build() {
        Client client = new Client();
        client.init();
        return client;
    }

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private ChannelInitializer<? extends SocketChannel> initializer;

    public void setInitializer(ChannelInitializer<? extends SocketChannel> initializer) {
        this.initializer = initializer;
    }

    private void init() {
        this.group = new NioEventLoopGroup();
        ChannelInitializer<? extends SocketChannel> initializer = null;
        if(this.initializer == null) {
            StatusHandler statusHandler = new StatusHandler();
            initializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(statusHandler)
                            .addLast(new Appender("\r\n".getBytes()));
                }
            };
        } else {
            initializer = this.initializer;
        }
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.group)
                .channel(NioSocketChannel.class)
                .handler(initializer);
    }

    public Channel connect(Config config) throws Exception {
        return this.bootstrap.connect(config.getHost(), config.getPort()).sync().channel();
    }

    public void shutdown() {
        this.group.shutdownGracefully();
    }
}