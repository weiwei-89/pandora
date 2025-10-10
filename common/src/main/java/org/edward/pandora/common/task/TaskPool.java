package org.edward.pandora.common.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskPool {
    private static final Logger logger = LoggerFactory.getLogger(TaskPool.class);
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private final Map<String, ScheduledFuture<?>> taskMap = new HashMap<>();
    private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(
            AVAILABLE_PROCESSORS,
            new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName(String.format("task-pool-%s", this.count.getAndIncrement()));
                    t.setDaemon(true);
                    return t;
                }
            });

    private TaskPool() {

    }

    private static class SingletonHolder {
        private static final TaskPool INSTANCE = new TaskPool();
    }

    public static TaskPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public synchronized void addTask(String taskName, Runnable task) {
        if(this.taskMap.containsKey(taskName)) {
            logger.info("task exists [{}]", taskName);
            return;
        }
        ScheduledFuture<?> future = this.pool.scheduleWithFixedDelay(
                task,
                1000,
                1000,
                TimeUnit.MILLISECONDS
        );
        this.taskMap.put(taskName, future);
        logger.info("task added [{}]", taskName);
    }

    public synchronized void stopTask(String taskName) {
        if(!this.taskMap.containsKey(taskName)) {
            logger.info("task does not exist [{}]", taskName);
            return;
        }
        ScheduledFuture<?> future = this.taskMap.get(taskName);
        future.cancel(true);
        this.taskMap.remove(taskName);
    }
}