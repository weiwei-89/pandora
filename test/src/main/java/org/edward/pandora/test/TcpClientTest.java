package org.edward.pandora.test;

import org.apache.commons.cli.*;
import org.edward.pandora.common.model.User;
import org.edward.pandora.common.netty.ext.client.*;
import org.edward.pandora.common.tcp.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientTest {
    private static final Logger logger = LoggerFactory.getLogger(TcpClientTest.class);
    private static final String TARGET_HOST = "target.host";
    private static final String TARGET_PORT = "target.port";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder().longOpt(TARGET_HOST).required(true).hasArg(true).build());
        options.addOption(Option.builder().longOpt(TARGET_PORT).required(true).hasArg(true).build());
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        String targetHost = cmd.getOptionValue(TARGET_HOST);
        int targetPort = Integer.parseInt(cmd.getOptionValue(TARGET_PORT));
        Config config = new Config();
        config.setHost(targetHost);
        config.setPort(targetPort);
        User user = new User();
        user.setName("edward");
        user.setPassword("123456");
        Client client = Client.build();
        Connector connector = null;
        Session session = null;
        try {
            connector = new Connector(client);
            session = connector.connect(config, user);
            session.send("hello");
            Thread.sleep(15000L);
//            session.close();
//            session.stop();
            connector.shutdown();
            Thread.sleep(30000L);
        } finally {
            if(session != null) {
                session.close();
            }
            if(connector != null) {
                connector.shutdown();
            }
        }
    }
}