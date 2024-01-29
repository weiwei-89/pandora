package org.edward.pandora.common.task;

public abstract class TriggeredTask implements Task, Runnable {
    abstract protected boolean trigger();

    protected void done(boolean result) {

    }

    protected void error(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void run() {
        boolean triggered = false;
        boolean result = false;
        try {
            triggered = this.trigger();
            if(triggered) {
                this.process();
                result = true;
            }
        } catch(Exception e) {
            result = false;
            this.error(e);
        } finally {
            if(triggered) {
                this.done(result);
            }
        }
    }
}