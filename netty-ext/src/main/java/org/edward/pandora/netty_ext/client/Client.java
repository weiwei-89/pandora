package org.edward.pandora.netty_ext.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.edward.pandora.netty_ext.codec.encoder.Appender;
import org.edward.pandora.netty_ext.handler.StatusHandler;
import org.edward.pandora.common.tcp.Config;
import org.edward.pandora.common.tcp.TcpClient;

public class Client implements TcpClient<Channel> {
    private Client() {

    }

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private ChannelInitializer<? extends SocketChannel> initializer;

    @Override
    public Channel connect(Config config) throws Exception {
        return this.bootstrap.connect(config.getHost(), config.getPort()).sync().channel();
    }

    @Override
    public void shutdown() {
        this.group.shutdownGracefully();
    }

    public EventLoopGroup getGroup() {
        return this.group;
    }

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

    public static Client build() {
        Client client = new Client();
        client.init();
        return client;
    }
}