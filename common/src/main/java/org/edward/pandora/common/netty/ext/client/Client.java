package org.edward.pandora.common.netty.ext.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    private static Client client;

    public static synchronized Client build() {
        if(client == null) {
            client = new Client();
        }
        client.startup();
        return client;
    }

    public static Client getClient() {
        return client;
    }

    private EventLoopGroup group;
    private Bootstrap bootstrap;

    private void startup() {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                    }
                });
    }

    public Channel connect(Config config) throws Exception {
        return this.bootstrap.connect(config.getHost(), config.getPort()).sync().channel();
    }

    public void shutdown() {
        this.group.shutdownGracefully();
    }
}