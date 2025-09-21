package org.edward.pandora.monkey.model;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Element> store;

    public Environment() {
        this.store = new HashMap<>();
    }

    public Element get(String name) {
        return this.store.get(name);
    }

    public void set(String name, Element value) {
        this.store.put(name, value);
    }
}