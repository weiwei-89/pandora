package org.edward.pandora.event_bus;

public class Event {
    private final String name;

    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}