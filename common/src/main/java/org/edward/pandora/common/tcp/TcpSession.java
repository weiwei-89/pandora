package org.edward.pandora.common.tcp;

public interface TcpSession<C> {
    C connect() throws Exception;

    boolean isActive();

    void send(String info) throws Exception;

    void close() throws Exception;
}