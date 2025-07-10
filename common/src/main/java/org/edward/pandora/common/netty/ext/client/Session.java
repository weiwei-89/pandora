package org.edward.pandora.common.netty.ext.client;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import org.edward.pandora.common.model.User;

public class Session {
    private final Client client;
    private final Config config;

    private Session(Client client, Config config) {
        this.client = client;
        this.config = config;
    }

    public static Session create(Config config) {
        Session session = new Session(Client.build(), config);
        try {
            session.init();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return session;
    }

    private Channel channel;

    private void init() throws Exception {
        this.channel = this.client.connect(this.config);
    }

    public void login(User user) {
        this.channel.writeAndFlush(this.channel.alloc().buffer().writeBytes(JSON.toJSONString(user).getBytes()));
    }

    public void send(byte[] data) {
        this.channel.writeAndFlush(this.channel.alloc().buffer().writeBytes(data));
    }

    public boolean isActive() {
        if(this.channel == null) {
            return false;
        }
        if(this.channel.isWritable()) {
            return true;
        }
        return false;
    }

    public void close() throws Exception {
        if(this.channel == null) {
            return;
        }
        this.channel.close().sync();
        this.channel = null;
    }

    public void shutdown() throws Exception {
        this.client.shutdown();
    }
}