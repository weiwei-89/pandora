package org.edward.pandora.onion.value;

public class CharacterValue implements Value {
    private final Character value;

    public CharacterValue(Character value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return Type.CHARACTER;
    }

    @Override
    public String inspect() {
        if(value == null) {
            return "";
        }
        return this.value.toString();
    }

    public Character get() {
        return this.value;
    }
}