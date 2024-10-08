package org.edward.pandora.common.netty.ext.server;

public class Config {
    private int port;
    private long readTimeout;
    private long writeTimeout;
    private long readWriteTimeout;

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public long getReadTimeout() {
        return readTimeout;
    }
    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }
    public long getWriteTimeout() {
        return writeTimeout;
    }
    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
    public long getReadWriteTimeout() {
        return readWriteTimeout;
    }
    public void setReadWriteTimeout(long readWriteTimeout) {
        this.readWriteTimeout = readWriteTimeout;
    }
}