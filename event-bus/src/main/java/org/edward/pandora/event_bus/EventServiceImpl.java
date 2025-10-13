package org.edward.pandora.event_bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements IEventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Override
    public void register(String app) throws Exception {
        EventListener.register(app);
    }

    @Override
    public void subscribe(String app, String eventName) throws Exception {
        EventListener listener = EventListener.acquire(app);
        listener.add(new Event(eventName));
    }
}