package org.edward.pandora.turbosnail.xml.model;

public class Decode extends Element {
    private Type type;
    private String value;
    private boolean option = false;
    private boolean protocol = false;

    public Type getType() {
        return this.type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public boolean isOption() {
        return option;
    }
    public void setOption(boolean option) {
        this.option = option;
    }
    public boolean isProtocol() {
        return protocol;
    }
    public void setProtocol(boolean protocol) {
        this.protocol = protocol;
    }

    public enum Type {
        EQUATION,
        HEX,
        ASCII,
        INT,
        INT_BE,
        INT_LE,
        SHORT,
        SHORT_BE,
        SHORT_LE,
        LONG,
        LONG_BE,
        LONG_LE,
        DOUBLE,
        DOUBLE_BE,
        DOUBLE_LE;

        public static Type get(String type) {
            for(Type t : Type.values()) {
                if(t.name().equalsIgnoreCase(type)) {
                    return t;
                }
            }
            return null;
        }
    }
}