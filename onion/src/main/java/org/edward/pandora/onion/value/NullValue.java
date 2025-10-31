package org.edward.pandora.onion.value;

public enum NullValue implements Value {
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