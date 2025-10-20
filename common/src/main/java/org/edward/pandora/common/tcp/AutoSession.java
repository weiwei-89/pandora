package org.edward.pandora.common.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AutoSession<C> extends CommonSession<C> {
    private static final Logger logger = LoggerFactory.getLogger(AutoSession.class);

    private ScheduledFuture<?> schedule;

    public AutoSession(ScheduledExecutorService executor) {
        this.init(executor);
    }

    private void init(ScheduledExecutorService executor) {
        this.schedule = executor.scheduleWithFixedDelay(
                new SessionTask<>(this),
                1000,
                1000,
                TimeUnit.MILLISECONDS
        );
    }

    private static final class SessionTask<C> implements Runnable {
        private final CommonSession<C> session;

        public SessionTask(CommonSession<C> session) {
            this.session = session;
        }

        @Override
        public void run() {
            logger.debug("checking session......");
            if(this.session.isActive()) {
                return;
            }
            logger.info("current session is inactive, trying to establish......");
            try {
                this.session.close();
                this.session.init();
            } catch(Exception e) {
                logger.error("session establishment failed", e);
                return;
            }
            logger.info("session established successfully");
        }
    }

    @Override
    public void close() throws Exception {
        logger.info("clear auto session");
        this.clear();
    }

    private void clear() throws Exception {
        if(this.schedule != null) {
            this.schedule.cancel(false);
            this.schedule = null;
        }
    }
}