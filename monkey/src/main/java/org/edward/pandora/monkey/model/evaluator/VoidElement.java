package org.edward.pandora.monkey.model.evaluator;

import org.edward.pandora.monkey.model.Element;

public enum VoidElement implements Element {
    INSTANCE;

    @Override
    public Type type() {
        return Type.VOID;
    }

    @Override
    public String inspect() {
        return "void";
    }
}