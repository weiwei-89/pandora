package org.edward.pandora.netty_ext.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class StatusHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(StatusHandler.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("StatusHandler added [channel_id:{}]", ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel inactivated [channel_id:{}]", ctx.channel().id());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE) {
                logger.info("reading bytes timeout, closing channel...... [channel_id:{}]", ctx.channel().id());
                ctx.close();
            } else if(event.state() == IdleState.WRITER_IDLE) {
                logger.info("writing bytes timeout, closing channel...... [channel_id:{}]", ctx.channel().id());
                ctx.close();
            } else if(event.state() == IdleState.ALL_IDLE) {
                logger.info("reading/writing bytes timeout, closing channel...... [channel_id:{}]", ctx.channel().id());
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("StatusHandler error", cause);
    }
}