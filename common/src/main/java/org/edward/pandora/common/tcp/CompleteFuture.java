package org.edward.pandora.common.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompleteFuture implements SessionFuture {
    private static final Logger logger = LoggerFactory.getLogger(CompleteFuture.class);

    private volatile boolean complete = false;
    private FutureListener listener;

    @Override
    public void addListener(FutureListener listener) {
        this.listener = listener;
        if(this.complete) {
            this.notifyListener();
        }
    }

    public void complete() {
        this.complete = true;
        this.notifyListener();
    }

    public void error(Throwable cause) {
        this.complete = true;
        this.listener.onError(cause);
    }

    private void notifyListener() {
        if(this.listener == null) {
            return;
        }
        try {
            this.listener.onComplete();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}