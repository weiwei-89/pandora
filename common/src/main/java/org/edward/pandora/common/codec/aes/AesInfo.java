package org.edward.pandora.common.codec.aes;

public class AesInfo {
    private final byte[] data;
    private final byte[] iv;

    public AesInfo(byte[] data, byte[] iv) {
        this.data = data;
        this.iv = iv;
    }

    public byte[] getData() {
        return data;
    }
    public byte[] getIv() {
        return iv;
    }
}