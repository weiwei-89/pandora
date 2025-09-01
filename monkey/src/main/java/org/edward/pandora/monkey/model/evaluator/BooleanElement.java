package org.edward.pandora.monkey.model.evaluator;

import org.edward.pandora.monkey.model.Element;

public class BooleanElement implements Element {
    private final boolean value;
    public static final BooleanElement TRUE = new BooleanElement(true);
    public static final BooleanElement FALSE = new BooleanElement(false);

    private BooleanElement(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public Type type() {
        return Type.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(this.value);
    }

    public static BooleanElement of(boolean value) {
        if(value) {
            return TRUE;
        }
        return FALSE;
    }
}