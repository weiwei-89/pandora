package org.edward.pandora.common.codec;

public class HybridResult {
    private final byte[] data;
    private final byte[] aesKey;
    private final byte[] iv;
    private final byte[] signature;
    private final long timestamp;

    public HybridResult(byte[] data, byte[] aesKey, byte[] iv, byte[] signature) {
        this.data = data;
        this.aesKey = aesKey;
        this.iv = iv;
        this.signature = signature;
        this.timestamp = System.currentTimeMillis();
    }

    public byte[] getData() {
        return data;
    }
    public byte[] getAesKey() {
        return aesKey;
    }
    public byte[] getIv() {
        return iv;
    }
    public byte[] getSignature() {
        return signature;
    }
    public long getTimestamp() {
        return timestamp;
    }
}