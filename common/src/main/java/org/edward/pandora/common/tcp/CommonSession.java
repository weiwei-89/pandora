package org.edward.pandora.common.tcp;

public abstract class CommonSession<C> implements TcpSession {
    private C connection;

    public void init() throws Exception {
        this.connection = this.connect();
    }

    public abstract C connect() throws Exception;

    public C getConnection() {
        return this.connection;
    }

    public void setConnection(C connection) {
        this.connection = connection;
    }
}