package org.edward.pandora.common.netty.ext.client;

import org.edward.pandora.common.model.User;
import org.edward.pandora.common.tcp.Config;
import org.edward.pandora.common.tcp.TcpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Connector {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);

    private final Client client;
    private final Map<String, Session> sessionMap;

    public Connector(Client client) {
        this.client = client;
        this.sessionMap = new HashMap<>();
    }

    private static String generateSessionId(Config config) {
        return String.format("%s:%d", config.getHost(), config.getPort());
    }

    public synchronized Session connect(Config config, User user) throws Exception {
        String sessionId = generateSessionId(config);
        if(this.sessionMap.containsKey(sessionId)) {
            return this.sessionMap.get(sessionId);
        }
        Session session = Session.create(this.client, config, user);
        this.sessionMap.put(sessionId, session);
        return session;
    }

    public void shutdown() {
        logger.info("shutting down connector......");
        if(this.client == null) {
            logger.info("done(not started)");
            return;
        }
        this.client.shutdown();
        logger.info("done");
    }
}