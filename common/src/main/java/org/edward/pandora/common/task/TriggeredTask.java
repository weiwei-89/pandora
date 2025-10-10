package org.edward.pandora.common.task;

public abstract class TriggeredTask {
    protected boolean trigger() {
        return false;
    }

    protected void process() {

    }
}