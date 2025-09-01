package org.edward.pandora.common.netty.ext.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ElasticFrameDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ElasticFrameDecoder.class);

    private final byte[] delimiter;
    private final int maxLength;

    public ElasticFrameDecoder(byte[] delimiter, int maxLength) {
        this.delimiter = delimiter;
        this.maxLength = maxLength;
    }

    private boolean findDelimiter = false;
    private int delimiterIndex = -1;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("ElasticFrameDecoder added");
        if(this.maxLength <= this.delimiter.length) {
            throw new Exception("the max length of one frame must be greater than the delimiter");
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < this.delimiter.length) {
            return;
        }
        if(this.findDelimiter) {
            ByteBuf target = this.findTarget(in);
            if(target == null) {
                return;
            }
            out.add(target);
            this.findDelimiter = false;
        } else {
            this.delimiterIndex = ByteBufUtil.index(in, this.delimiter);
            if(this.delimiterIndex >= 0) {
                this.findDelimiter = true;
                if(this.delimiterIndex > 0) {
                    in.skipBytes(this.delimiterIndex-in.readerIndex());
                    in.discardReadBytes();
                    this.delimiterIndex = 0;
                }
                ByteBuf target = this.findTarget(in);
                if(target == null) {
                    return;
                }
                out.add(target);
                this.findDelimiter = false;
            } else {
                this.findDelimiter = false;
                in.skipBytes(in.readableBytes()-this.delimiter.length);
                in.discardReadBytes();
            }
        }
    }

    private ByteBuf findTarget(ByteBuf in) {
        if(in.readableBytes() < this.delimiter.length*2) {
            return null;
        }
        ByteBuf cache = in.slice(this.delimiter.length, in.readableBytes()-this.delimiter.length);
        int nextDelimiterIndex = ByteBufUtil.index(cache, this.delimiter);
        if(nextDelimiterIndex >= 0) {
            int middleLength = this.delimiter.length + nextDelimiterIndex;
            if(middleLength >= this.maxLength) {
                return in.readRetainedSlice(this.maxLength);
            } else {
                return in.readRetainedSlice(middleLength);
            }
        } else {
            if(in.readableBytes() >= this.maxLength) {
                return in.readRetainedSlice(this.maxLength);
            }
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ElasticFrameDecoder error", cause);
    }
}