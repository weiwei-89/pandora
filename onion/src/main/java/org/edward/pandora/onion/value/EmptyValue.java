package org.edward.pandora.onion.value;

public enum EmptyValue implements Value {
    INSTANCE;

    @Override
    public Value.Type type() {
        return Value.Type.EMPTY;
    }

    @Override
    public String inspect() {
        return "";
    }
}