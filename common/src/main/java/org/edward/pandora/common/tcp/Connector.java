package org.edward.pandora.common.tcp;

import org.edward.pandora.common.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class Connector<C> {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);

    private final TcpClient<C> client;
    private final Map<String, CommonSession<C>> sessions;

    public Connector(TcpClient<C> client) {
        this.client = client;
        this.sessions = new HashMap<>();
    }

    protected abstract CommonSession<C> buildSession(TcpClient<C> client, Config config, User user);

    private static String generateSessionId(Config config) {
        return String.format("%s:%d", config.getHost(), config.getPort());
    }

    public synchronized void connect(Config config, User user) throws Exception {
        String sessionId = generateSessionId(config);
        if(this.sessions.containsKey(sessionId)) {
            return;
        }
        CommonSession<C> session = this.buildSession(this.client, config, user);
        try {
            logger.info("current session is inactive, trying to establish(1st)......");
            session.init();
        } catch(Exception e) {
            logger.error("session establishment(1st) failed", e);
        }
        this.sessions.put(sessionId, session);
    }

    public SessionFuture send(Config config, String info) throws Exception {
        String sessionId = generateSessionId(config);
        if(!this.sessions.containsKey(sessionId)) {
            throw new Exception("session is not initialized");
        }
        CommonSession<C> session = this.sessions.get(sessionId);
        return session.send(info);
    }

    public synchronized void close(Config config) throws Exception {
        String sessionId = generateSessionId(config);
        if(!this.sessions.containsKey(sessionId)) {
            return;
        }
        CommonSession<C> session = this.sessions.get(sessionId);
        session.close();
        this.sessions.remove(sessionId);
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