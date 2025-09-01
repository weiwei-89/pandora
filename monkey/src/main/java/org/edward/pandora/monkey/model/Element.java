package org.edward.pandora.monkey.model;

public interface Element {
    enum Type {
        INTEGER, BOOLEAN, NULL, RETURN
    }

    Type type();

    String inspect();
}