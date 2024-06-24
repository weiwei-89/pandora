package org.edward.pandora.common.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonTaskPool {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(
            AVAILABLE_PROCESSORS,
            new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName(String.format("common-task-pool-%s", this.count.getAndIncrement()));
                    thread.setDaemon(true);
                    return thread;
                }
            });
}