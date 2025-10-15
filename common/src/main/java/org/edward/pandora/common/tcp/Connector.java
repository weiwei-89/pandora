package org.edward.pandora.common.tcp;

import org.edward.pandora.common.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class Connector<C> {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);

    private final Map<String, CommonSession<C>> sessions;

    public Connector() {
        this.sessions = new HashMap<>();
    }

    protected abstract CommonSession<C> buildSession(Config config, User user);

    private static String generateSessionId(Config config) {
        return String.format("%s:%d", config.getHost(), config.getPort());
    }

    public synchronized CommonSession<C> connect(Config config, User user) {
        String sessionId = generateSessionId(config);
        if(this.sessions.containsKey(sessionId)) {
            return this.sessions.get(sessionId);
        }
        CommonSession<C> session = this.buildSession(config, user);
        try {
            logger.info("current session is inactive, trying to establish(1st)......");
            session.init();
        } catch(Exception e) {
            logger.error("session establishment(1st) failed", e);
        }
        this.sessions.put(sessionId, session);
        return session;
    }

//    public void shutdown() {
//        logger.info("shutting down connector......");
//        if(this.client == null) {
//            logger.info("done(not started)");
//            return;
//        }
//        this.client.shutdown();
//        logger.info("done");
//    }
}