package org.edward.pandora.common.tcp;

public interface TcpSession<C> {
    C connect() throws Exception;

    boolean isActive();

    SessionFuture send(String info) throws Exception;

    void close() throws Exception;
}