package org.edward.pandora.common.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AutoSession<C> extends CommonSession<C> {
    private static final Logger logger = LoggerFactory.getLogger(AutoSession.class);

    private ScheduledFuture<?> schedule;

    public void init(ScheduledExecutorService executor) throws Exception {
        super.init();
        this.schedule = executor.scheduleWithFixedDelay(
                new SessionTask<C>(this),
                1000,
                1000,
                TimeUnit.MILLISECONDS
        );
    }

    public void stop() {
        this.close();
        if(this.schedule != null) {
            this.schedule.cancel(false);
            this.schedule = null;
        }
    }

    private static final class SessionTask<C> implements Runnable {
        private final AutoSession<C> session;

        public SessionTask(AutoSession<C> session) {
            this.session = session;
        }

        @Override
        public void run() {
            logger.debug("checking session......");
            if(this.session.isActive()) {
                return;
            }
            logger.info("current session is inactive, trying to establish......");
            C connection = null;
            try {
                this.session.close();
                connection = this.session.connect();
            } catch(Exception e) {
                logger.error("session establishment failed", e);
                return;
            }
            logger.info("session established successfully");
            this.session.setConnection(connection);
        }
    }
}