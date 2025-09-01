package org.edward.pandora.monkey.model.evaluator;

import org.edward.pandora.monkey.model.Element;

public enum NullElement implements Element {
    INSTANCE;

    @Override
    public Type type() {
        return Type.NULL;
    }

    @Override
    public String inspect() {
        return "null";
    }
}