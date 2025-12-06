package org.edward.pandora.netty_ext.util;

import io.netty.buffer.ByteBuf;

public class ByteBufUtil {
    public static byte[] getReadableBytes(ByteBuf buffer) {
        ByteBuf part = buffer.slice();
        byte[] bytes = new byte[part.readableBytes()];
        part.readBytes(bytes);
        return bytes;
    }

    public static int index(ByteBuf buffer, byte[] bytes) {
        boolean find = false;
        for(int i=buffer.readerIndex(); i<=buffer.readerIndex()+buffer.readableBytes()-bytes.length; i++) {
            ByteBuf part = buffer.slice(i, bytes.length);
            for(int d=0; d<bytes.length; d++) {
                if(part.getByte(d) == bytes[d]) {
                    find = true;
                } else {
                    find = false;
                    break;
                }
            }
            if(find) {
                return i;
            }
        }
        return -1;
    }
}