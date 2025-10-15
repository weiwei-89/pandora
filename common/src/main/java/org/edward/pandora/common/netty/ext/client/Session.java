package org.edward.pandora.common.netty.ext.client;

import io.netty.channel.Channel;
import org.edward.pandora.common.model.User;
import org.edward.pandora.common.task.ScheduledTaskPool;
import org.edward.pandora.common.tcp.AutoSession;
import org.edward.pandora.common.tcp.Config;
import org.edward.pandora.common.tcp.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

public class Session extends AutoSession<Channel> {
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    private final TcpClient<Channel> client;
    private final Config config;
    private final User user;

    private Session(
            TcpClient<Channel> client,
            Config config,
            User user
    ) {
        super(ScheduledTaskPool.getInstance().getPool());
        this.client = client;
        this.config = config;
        this.user = user;
    }

    private Session(
            ScheduledExecutorService executor,
            TcpClient<Channel> client,
            Config config,
            User user
    ) {
        super(executor);
        this.client = client;
        this.config = config;
        this.user = user;
    }

    @Override
    public Channel connect() throws Exception {
        if(this.client == null) {
            return null;
        }
        return this.client.connect(this.config);
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
    public void send(String info) throws Exception {
        logger.info("sending info......");
        this.getConnection().writeAndFlush(this.getConnection().alloc().buffer().writeBytes(info.getBytes()));
    }

    @Override
    public void close() throws Exception {
        if(this.getConnection() == null) {
            return;
        }
        this.getConnection().close();
    }

    public static Session create(
            TcpClient<Channel> client,
            Config config,
            User user
    ) {
        return new Session(client, config, user);
    }

    public static Session create(
            ScheduledExecutorService executor,
            TcpClient<Channel> client,
            Config config,
            User user
    ) {
        return new Session(executor, client, config, user);
    }

//    private void login() throws Exception {
//        this.getConnection().writeAndFlush(this.getConnection().alloc().buffer().writeBytes(JSON.toJSONString(this.user).getBytes())).sync();
//    }
}