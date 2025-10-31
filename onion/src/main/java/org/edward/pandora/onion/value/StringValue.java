package org.edward.pandora.onion.value;

public class StringValue implements Value {
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return Type.STRING;
    }

    @Override
    public String inspect() {
        return this.value;
    }

    public String get() {
        return this.value;
    }
}