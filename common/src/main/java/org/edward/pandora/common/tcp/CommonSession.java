package org.edward.pandora.common.tcp;

public abstract class CommonSession<C> implements TcpSession<C> {
    private C connection;

    public void init() throws Exception {
        this.connection = this.connect();
    }

    public C getConnection() {
        return this.connection;
    }
}