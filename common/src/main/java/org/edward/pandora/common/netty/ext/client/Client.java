package org.edward.pandora.common.netty.ext.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.edward.pandora.common.netty.ext.codec.encoder.Appender;

public class Client {
    private static Client client;

    public static synchronized Client build() {
        if(client == null) {
            client = new Client();
        }
        return client;
    }

    public static Client getClient() {
        return client;
    }

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private ChannelInitializer<? extends SocketChannel> initializer;

    public void setInitializer(ChannelInitializer<? extends SocketChannel> initializer) {
        this.initializer = initializer;
    }

    public void startup() {
        this.group = new NioEventLoopGroup();
        ChannelInitializer<? extends SocketChannel> initializer = null;
        if(this.initializer == null) {
            initializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
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