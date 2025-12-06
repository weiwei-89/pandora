package org.edward.pandora.netty_ext.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Heartbeater extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(Heartbeater.class);

    private final long interval;

    public Heartbeater(long interval) {
        this.interval = interval;
    }

    private long lastPulseTime;
    private ScheduledFuture<?> schedule;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("Heartbeater added");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.init(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.stop();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Heartbeater error", cause);
    }

    private void init(ChannelHandlerContext ctx) {
        this.lastPulseTime = System.currentTimeMillis();
        this.schedule = ctx.executor().schedule(new HeartbeatTask(ctx), this.interval, TimeUnit.MILLISECONDS);
    }

    private void stop() {
        if(this.schedule != null) {
            this.schedule.cancel(false);
            this.schedule = null;
        }
    }

    private abstract static class TimerTask implements Runnable {
        private final ChannelHandlerContext ctx;

        TimerTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                this.execute(this.ctx);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        protected abstract void execute(ChannelHandlerContext ctx) throws Exception;
    }

    private final class HeartbeatTask extends TimerTask {
        HeartbeatTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void execute(ChannelHandlerContext ctx) throws Exception {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - lastPulseTime;
            if(timeElapsed >= interval) {
                lastPulseTime = currentTime;
                ctx.fireUserEventTriggered(HeartbeatEvent.PULSE);
                schedule = ctx.executor().schedule(this, interval, TimeUnit.MILLISECONDS);
            } else {
                schedule = ctx.executor().schedule(this, interval-timeElapsed, TimeUnit.MILLISECONDS);
            }
        }
    }

    public enum HeartbeatEvent {
        PULSE;
    }
}