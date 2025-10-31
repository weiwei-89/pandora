package org.edward.pandora.onion.value;

public class BooleanValue implements Value {
    private final Boolean value;

    public BooleanValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return Type.BOOLEAN;
    }

    @Override
    public String inspect() {
        if(value == null) {
            return "false";
        }
        return this.value.toString();
    }

    public Boolean get() {
        return this.value;
    }
}