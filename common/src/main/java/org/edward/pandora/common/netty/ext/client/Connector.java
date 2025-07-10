package org.edward.pandora.common.netty.ext.client;

import org.edward.pandora.common.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Connector {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);

    private final Client client;
    private final User user;
    private final Map<String, Session> sessionMap;
    private final ReadWriteLock sessionLock = new ReentrantReadWriteLock();

    public Connector(Client client, User user) {
        this.client = client;
        this.user = user;
        this.sessionMap = new HashMap<>();
    }

    private static String generateSessionId(Config config) {
        return String.format("%s:%d", config.getHost(), config.getPort());
    }

    private Session getSession(Config config) throws Exception {
        String sessionId = generateSessionId(config);
        this.sessionLock.readLock().lock();
        try {
            if(this.sessionMap.containsKey(sessionId)) {
                return this.sessionMap.get(sessionId);
            }
        } finally {
            this.sessionLock.readLock().unlock();
        }
        return null;
    }

    private Session connect(Config config) throws Exception {
        String sessionId = generateSessionId(config);
        Session session = Session.create(this.client.connect(config));
        session.login(this.user);
        this.sessionLock.writeLock().lock();
        try {
            this.sessionMap.remove(sessionId);
            this.sessionMap.put(sessionId, session);
        } finally {
            this.sessionLock.writeLock().unlock();
        }
        return session;
    }

    private void closeSession(Config config) {
        String sessionId = generateSessionId(config);
        this.sessionLock.writeLock().lock();
        try {
            if(this.sessionMap.containsKey(sessionId)) {
                Session session = this.sessionMap.get(sessionId);
                session.close();
                this.sessionMap.remove(sessionId);
            }
        } finally {
            this.sessionLock.writeLock().unlock();
        }
    }

    public void send(Config config, String info) throws Exception {
        logger.info("sending info......");
        // TODO 当多个线程同时调用这个方法时，会重复创建相同的会话
        Session session = this.getSession(config);
        if(session == null) {
            logger.info("there's no session, trying to connect......");
            session = this.connect(config);
            logger.info("session established");
        }
        if(!session.isActive()) {
            logger.info("current session is inactive, trying to reconnect......");
            this.closeSession(config);
            session = this.connect(config);
            logger.info("session established again");
        }
        session.send(info.getBytes());
        logger.info("done");
    }

    public void close(Config config) {
        logger.info("closing session......");
        this.closeSession(config);
        logger.info("done");
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