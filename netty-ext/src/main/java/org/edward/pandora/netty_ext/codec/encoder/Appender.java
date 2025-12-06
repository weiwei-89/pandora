package org.edward.pandora.netty_ext.codec.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Appender extends MessageToByteEncoder<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(Appender.class);

    private final byte[] data;

    public Appender(byte[] data) {
        this.data = data;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("Appender added");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        msg.writeBytes(this.data);
        out.writeBytes(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Appender error", cause);
    }
}