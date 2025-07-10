package org.edward.pandora.common.netty.ext.client;

import org.edward.pandora.common.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);

    private final Config config;
    private final User user;

    public Connector(Config config, User user) {
        this.config = config;
        this.user = user;
    }

    public Session session;

    public void send(String info) throws Exception {
        logger.info("sending info......");
        if(this.session == null) {
            logger.info("there's no session, trying to connect......");
            this.session = Session.create(this.config);
            this.session.login(this.user);
            logger.info("session established");
        }
        if(!this.session.isActive()) {
            logger.info("the session is inactive, trying to reconnect......");
            this.session.close();
            this.session = Session.create(this.config);
            this.session.login(this.user);
            logger.info("session established again");
        }
        this.session.send(info.getBytes());
        logger.info("done");
    }

    public void close() throws Exception {
        logger.info("closing session......");
        if(this.session == null) {
            logger.info("done(closed)");
            return;
        }
        this.session.close();
        logger.info("done");
    }

    public void shutdown() throws Exception {
        logger.info("shutting down connector......");
        if(this.session == null) {
            logger.info("done(not started)");
            return;
        }
        this.session.shutdown();
        this.session = null;
        logger.info("done");
    }
}