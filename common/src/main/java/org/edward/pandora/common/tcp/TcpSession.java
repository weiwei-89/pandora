package org.edward.pandora.common.tcp;

public interface TcpSession {
    boolean isActive();

    void send(String info);

    void close();
}