package org.edward.pandora.common.netty.ext.server;

import io.netty.channel.Channel;
import org.edward.pandora.common.model.User;

public class Session {
    private Channel channel;
    private User user;

    public Channel getChannel() {
        return channel;
    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}