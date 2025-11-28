package org.edward.pandora.common.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskPool {
    private static final Logger logger = LoggerFactory.getLogger(TaskPool.class);

    private final Map<String, Future<?>> taskMap = new HashMap<>();
    private final ExecutorService pool = Executors.newCachedThreadPool(
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
        return TaskPool.SingletonHolder.INSTANCE;
    }

    public ExecutorService getPool() {
        return this.pool;
    }

    public synchronized void addTask(String taskName, Runnable task) {
        if(this.taskMap.containsKey(taskName)) {
            logger.info("task exists [{}]", taskName);
            return;
        }
        Runnable innerTask = () -> {
            try {
                task.run();
            } catch(Exception e) {
                logger.error("task error", e);
            } finally {
                this.taskMap.remove(taskName);
            }
        };
        Future<?> future = this.pool.submit(innerTask);
        this.taskMap.put(taskName, future);
        logger.info("task added [{}]", taskName);
    }

    public synchronized void stopTask(String taskName) {
        if(!this.taskMap.containsKey(taskName)) {
            logger.info("task does not exist [{}]", taskName);
            return;
        }
        Future<?> future = this.taskMap.get(taskName);
        future.cancel(true);
        this.taskMap.remove(taskName);
    }
}