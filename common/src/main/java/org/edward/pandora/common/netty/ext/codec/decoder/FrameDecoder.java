package org.edward.pandora.common.netty.ext.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FrameDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(FrameDecoder.class);

    private final byte[] delimiter;
    private final int length;

    public FrameDecoder(byte[] delimiter, int length) {
        this.delimiter = delimiter;
        this.length = length;
    }

    private boolean findDelimiter = false;
    private int delimiterIndex = -1;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("FrameDecoder added");
        if(this.length <= this.delimiter.length) {
            throw new Exception("the length of one frame must be greater than the delimiter");
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < this.length) {
            return;
        }
        if(this.findDelimiter) {
            if(in.readableBytes() >= this.length) {
                out.add(in.readRetainedSlice(this.length));
                this.findDelimiter = false;
            }
        } else {
            this.delimiterIndex = ByteBufUtil.index(in, this.delimiter);
            if(this.delimiterIndex >= 0) {
                this.findDelimiter = true;
                if(this.delimiterIndex > 0) {
                    in.skipBytes(this.delimiterIndex-in.readerIndex());
                    in.discardReadBytes();
                    this.delimiterIndex = 0;
                }
                if(in.readableBytes() >= this.length) {
                    out.add(in.readRetainedSlice(this.length));
                    this.findDelimiter = false;
                }
            } else {
                this.findDelimiter = false;
                in.skipBytes(in.readableBytes()-this.delimiter.length);
                in.discardReadBytes();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("FrameDecoder error", cause);
    }
}