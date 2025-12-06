package org.edward.pandora.netty_ext.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.edward.pandora.common.model.User;
import org.edward.pandora.common.task.ScheduledTaskPool;
import org.edward.pandora.common.tcp.*;
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
    public SessionFuture send(String info) throws Exception {
        logger.info("sending info......");
        CompleteFuture completeFuture = new CompleteFuture();
        ChannelFuture future = this.getConnection().writeAndFlush(this.getConnection().alloc().buffer().writeBytes(info.getBytes()));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    completeFuture.complete();
                } else {
                    completeFuture.error(future.cause());
                }
            }
        });
        return completeFuture;
    }

    @Override
    public void close() throws Exception {
        super.close();
        logger.info("close session");
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