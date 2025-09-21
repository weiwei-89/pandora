package org.edward.pandora.monkey.model.evaluator;

import org.edward.pandora.monkey.model.Element;

public class VoidElement implements Element {
    @Override
    public Type type() {
        return Type.VOID;
    }

    @Override
    public String inspect() {
        return "";
    }
}