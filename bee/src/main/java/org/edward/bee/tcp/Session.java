package org.edward.bee.tcp;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import org.edward.pandora.common.model.User;

public class Session {
    private final Channel channel;

    private Session(Channel channel) {
        this.channel = channel;
    }

    public static Session create(Config config) throws Exception {
        return new Session(Client.getClient().connect(config));
    }

    public Session login(User user) {
        this.channel.writeAndFlush(this.channel.alloc().buffer().writeBytes(JSON.toJSONString(user).getBytes()));
        return this;
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
    }
}