package org.edward.pandora.common.task;

public class TaskConfig {
    private boolean enable;
    private long interval;
    private long timeout;

    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public long getInterval() {
        return interval;
    }
    public void setInterval(long interval) {
        this.interval = interval;
    }
    public long getTimeout() {
        return timeout;
    }
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}