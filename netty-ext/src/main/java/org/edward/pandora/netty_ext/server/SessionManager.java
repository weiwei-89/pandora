package org.edward.pandora.netty_ext.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static void addSession(Session session) {
        sessions.put(session.getChannel().id().asLongText(), session);
    }

    public static Session getSession(Channel channel) {
        return sessions.get(channel.id().asLongText());
    }
}