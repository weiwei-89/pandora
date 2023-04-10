package org.edward.pandora.turbosnail.decoder.model;

public class Data {
    private final byte[] bytes;
    private final int capacity;

    public Data(byte[] bytes) {
        this.bytes = bytes;
        this.capacity = bytes.length;
    }

    private int readerIndex;

    public int getReaderIndex() {
        return this.readerIndex;
    }

    public byte[] read(int count) throws Exception {
        byte[] partBytes = new byte[count];
        for(int i=this.readerIndex; i<this.readerIndex+count; i++) {
            partBytes[i-this.readerIndex] = this.bytes[i];
        }
        this.readerIndex += count;
        return partBytes;
    }

    public void skip(int count) throws Exception {
        this.readerIndex += count;
    }
}