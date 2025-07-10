package org.edward.pandora.test;

import org.edward.pandora.common.model.User;
import org.edward.pandora.common.netty.ext.client.Client;
import org.edward.pandora.common.netty.ext.client.Config;
import org.edward.pandora.common.netty.ext.client.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientTest {
    private static final Logger logger = LoggerFactory.getLogger(TcpClientTest.class);

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setHost("localhost");
        config.setPort(8090);
        User user = new User();
        user.setName("edward");
        user.setPassword("123456");
        Client client = Client.build();
        Connector connector = null;
        try {
            connector = new Connector(client, user);
            connector.send(config, "hello");
//            connector.close(config);
        } finally {
            if(connector != null) {
//                connector.shutdown();
            }
        }
    }
}