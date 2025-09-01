package org.edward.pandora.monkey.model.evaluator;

import org.edward.pandora.monkey.model.Element;

public class ReturnElement implements Element {
    private final Element value;

    public ReturnElement(Element value) {
        this.value = value;
    }

    public Element getValue() {
        return this.value;
    }

    @Override
    public Type type() {
        return Type.RETURN;
    }

    @Override
    public String inspect() {
        return this.value.inspect();
    }
}