package org.edward.pandora.monkey.model;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Element> store;
    private final Environment outer;

    public Environment() {
        this.store = new HashMap<>();
        this.outer = null;
    }

    public Environment(Environment outer) {
        this.store = new HashMap<>();
        this.outer = outer;
    }

    public Element get(String name) {
        Element value = this.store.get(name);
        if(value != null) {
            return value;
        }
        if(this.outer != null) {
            return this.outer.get(name);
        }
        return null;
    }

    public void set(String name, Element value) {
        this.store.put(name, value);
    }
}