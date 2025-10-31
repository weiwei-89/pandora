package org.edward.pandora.onion.value;

public class NumberValue implements Value {
    private final Number value;

    public NumberValue(Number value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return Type.NUMBER;
    }

    @Override
    public String inspect() {
        if(value == null) {
            return "0";
        }
        return value.toString();
    }

    public Number get() {
        return this.value;
    }
}