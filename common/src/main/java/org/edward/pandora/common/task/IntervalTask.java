package org.edward.pandora.common.task;

public abstract class IntervalTask extends TriggeredTask {
    private final long interval;

    public IntervalTask(long interval) {
        this.interval = interval;
    }

    private long lastProcessTime = System.currentTimeMillis();

    @Override
    protected boolean trigger() {
        if(System.currentTimeMillis() >= this.lastProcessTime+this.interval) {
            return true;
        }
        return false;
    }

    @Override
    protected void done(boolean result) {
        this.lastProcessTime = System.currentTimeMillis();
    }
}