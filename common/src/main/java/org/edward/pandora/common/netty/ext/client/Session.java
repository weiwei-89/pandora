package org.edward.pandora.common.netty.ext.client;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import org.edward.pandora.common.model.User;
import org.edward.pandora.common.tcp.AutoSession;
import org.edward.pandora.common.tcp.Config;
import org.edward.pandora.common.tcp.TcpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session extends AutoSession<Channel> implements TcpSession {
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    private final Client client;
    private final Config config;
    private final User user;

    private Session(Client client, Config config, User user) {
        this.client = client;
        this.config = config;
        this.user = user;
    }

    @Override
    public boolean isActive() {
        if(this.getConnection() == null) {
            return false;
        }
        if(this.getConnection().isWritable()) {
            return true;
        }
        return false;
    }

    @Override
    public void send(String info) {
        logger.info("sending info......");
        this.getConnection().writeAndFlush(this.getConnection().alloc().buffer().writeBytes(info.getBytes()));
    }

    @Override
    public void close() {
        if(this.getConnection() == null) {
            return;
        }
        this.getConnection().close();
    }

    @Override
    public Channel connect() throws Exception {
        return this.client.connect(this.config);
    }

    public static Session create(Client client, Config config, User user) throws Exception {
        Session session = new Session(client, config, user);
        session.init(client.getGroup());
        session.login();
        return session;
    }

    private void login() throws Exception {
        this.getConnection().writeAndFlush(this.getConnection().alloc().buffer().writeBytes(JSON.toJSONString(this.user).getBytes())).sync();
    }
}