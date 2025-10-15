package org.edward.pandora.common.task;

public abstract class CommonTask implements Runnable {
    private final Processor processor;

    public CommonTask(Processor processor) {
        this.processor = processor;
    }

    protected abstract boolean trigger();

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
                if(this.processor == null) {
                    throw new Exception("processor is not set");
                }
                this.processor.process();
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