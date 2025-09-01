package org.edward.pandora.monkey.model.evaluator;

import org.edward.pandora.monkey.model.Element;

public class IntegerElement implements Element {
    private final int value;

    public IntegerElement(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public Type type() {
        return Type.INTEGER;
    }

    @Override
    public String inspect() {
        return String.valueOf(this.value);
    }
}