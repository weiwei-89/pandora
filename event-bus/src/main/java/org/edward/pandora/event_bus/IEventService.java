package org.edward.pandora.event_bus;

public interface IEventService {
    void register(String app) throws Exception;

    void subscribe(String app, String eventName) throws Exception;
}