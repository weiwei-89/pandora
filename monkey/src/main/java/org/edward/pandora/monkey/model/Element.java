package org.edward.pandora.monkey.model;

public interface Element {
    enum Type {
        INTEGER, BOOLEAN, NULL, RETURN, VOID
    }

    Type type();

    String inspect();
}