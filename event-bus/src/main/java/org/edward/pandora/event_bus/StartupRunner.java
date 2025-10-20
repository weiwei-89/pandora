package org.edward.pandora.event_bus;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.edward.pandora.common.model.User;
import org.edward.pandora.common.netty.ext.client.Client;
import org.edward.pandora.common.tcp.*;
import org.edward.pandora.common.netty.ext.client.Session;
import org.edward.pandora.common.netty.ext.handler.Heartbeater;
import org.edward.pandora.common.netty.ext.handler.IdleHandler;
import org.edward.pandora.common.netty.ext.handler.StatusHandler;
import org.edward.pandora.common.netty.ext.server.Server;
import org.edward.pandora.common.netty.ext.server.SessionManager;
import org.edward.pandora.common.netty.ext.util.ByteBufUtil;
import org.edward.pandora.common.task.*;
import org.edward.pandora.common.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

@Order(1)
@Component
public class StartupRunner implements ApplicationRunner {
    private final TaskPool taskPool = TaskPool.getInstance();
    private final ScheduledTaskPool scheduledTaskPool = ScheduledTaskPool.getInstance();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.taskPool.addTask("tcp-server", new GeneralTask(new TcpServerProcessor()));
        this.scheduledTaskPool.addTask("scanning", new IntervalTask(new ScanningProcessor(), 5*1000));
    }

    private static class TcpServerProcessor implements Processor {
        private static final Logger logger = LoggerFactory.getLogger(TcpServerProcessor.class);

        @Override
        public void init() throws Exception {

        }

        @Override
        public void process() throws Exception {
            org.edward.pandora.common.netty.ext.server.Config config = new org.edward.pandora.common.netty.ext.server.Config();
            config.setPort(8090);
            StatusHandler statusHandler = new StatusHandler();
            Server server = new Server(config);
            server.setInitializer(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new IdleHandler(
                                    100000L,
                                    0,
                                    0,
                                    TimeUnit.MILLISECONDS)
                            )
                            .addLast(statusHandler)
                            .addLast(new Heartbeater(100L))
                            .addLast(new LineBasedFrameDecoder(512))
                            .addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    if(evt instanceof Heartbeater.HeartbeatEvent) {
//                                        logger.info("tick......");
                                    }
                                    super.userEventTriggered(ctx, evt);
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    org.edward.pandora.common.netty.ext.server.Session session = new org.edward.pandora.common.netty.ext.server.Session();
                                    session.setChannel(ctx.channel());
                                    SessionManager.addSession(session);
                                    super.channelActive(ctx);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    try {
                                        if(msg instanceof ByteBuf) {
                                            ByteBuf buffer = (ByteBuf) msg;
                                            logger.info("hex: {}",
                                                    DataUtil.toHexString(ByteBufUtil.getReadableBytes(buffer)));
                                        }
                                    } finally {
                                        ReferenceCountUtil.release(msg, ReferenceCountUtil.refCnt(msg));
                                    }
                                }
                            });
                }
            });
            server.startup();
        }
    }

    private static class ScanningProcessor implements Processor {
        private static final Logger logger = LoggerFactory.getLogger(ScanningProcessor.class);
        private static final String APP_BASE_FOLDER_PATH = "D:\\edward\\test\\pandora\\event-bus\\app";
        private static final Connector<Channel> connector = new Connector<Channel>(Client.build()) {
            @Override
            protected CommonSession<Channel> buildSession(TcpClient<Channel> client, Config config, User user) {
                Client client0 = (Client) client;
                return Session.create(client0.getGroup(), client, config, user);
            }
        };

        private Config config;

        @Override
        public void init() throws Exception {
            Config config = new Config();
            config.setHost("localhost");
            config.setPort(8090);
            this.config = config;
            User user = new User();
            user.setName("edward");
            user.setPassword("123456");
            connector.connect(config, user);
        }

        @Override
        public void process() throws Exception {
            File appBaseFolder = new File(APP_BASE_FOLDER_PATH);
            if(!appBaseFolder.exists()) {
                logger.warn("app base folder does not exist");
                return;
            }
            if(!appBaseFolder.isDirectory()) {
                logger.warn("{} is not a folder", APP_BASE_FOLDER_PATH);
                return;
            }
            File[] appFolderArray = appBaseFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });
            if(appFolderArray==null || appFolderArray.length==0) {
                logger.info("there are no apps");
                return;
            }
            logger.info("there are {} apps", appFolderArray.length);
            for(int i=0; i<appFolderArray.length; i++) {
                File appFolder = appFolderArray[i];
                logger.info("{}.handling app \"{}\"", i+1, appFolder.getName());
                this.handleApp(appFolder);
            }
        }

        private void handleApp(File file) throws Exception {
            File[] eventFileArray = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".txt");
                }
            });
            if(eventFileArray==null || eventFileArray.length==0) {
                logger.info("there are no events");
                return;
            }
            logger.info("there are {} events", eventFileArray.length);
            for(int i=0; i<eventFileArray.length; i++) {
                File eventFile = eventFileArray[i];
                logger.info("{}.processing event \"{}\"", i+1, eventFile.getName());
                this.handleEvent(eventFile);
            }
        }

        private void handleEvent(File file) throws Exception {
            SessionFuture future = connector.send(this.config, "hello");
            future.addListener(new FutureListener() {
                @Override
                public void onComplete() throws Exception {
                    logger.info("server notification completed");
                    // TODO 迁移到服务端去做
                    Path targetPath = file.toPath().getParent().resolve("in_progress"+File.separator+file.getName());
                    Files.createDirectories(targetPath.getParent());
                    Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                }

                @Override
                public void onError(Throwable cause) {
                    logger.error(cause.getMessage(), cause);
                }
            });
        }
    }
}