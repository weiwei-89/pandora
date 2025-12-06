package org.edward.pandora.test;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import org.apache.commons.cli.*;
import org.edward.pandora.common.http.ApiLoader;
import org.edward.pandora.netty_ext.handler.HttpDispatchHandler;
import org.edward.pandora.netty_ext.handler.HttpJsonHandler;
import org.edward.pandora.netty_ext.handler.HttpResponseHandler;
import org.edward.pandora.netty_ext.handler.StatusHandler;
import org.edward.pandora.netty_ext.server.Config;
import org.edward.pandora.netty_ext.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerTest.class);
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
        ApiLoader apiLoader = new ApiLoader("org.edward.pandora.test.controller");
        apiLoader.init();
        Server server = new Server(config);
        server.setInitializer(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(statusHandler)
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(1024*1024))
                        .addLast(new HttpJsonHandler())
                        .addLast(new HttpDispatchHandler(apiLoader))
                        .addLast(new HttpResponseHandler());
            }
        });
        server.startup();
    }
}