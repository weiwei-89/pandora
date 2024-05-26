package org.edward.pandora.turbosnail.xml.model;

public class Decode extends Element {
    private Type type;
    private Process process;
    private String value;
    private boolean protocol = false;

    public Type getType() {
        return this.type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public Process getProcess() {
        return process;
    }
    public void setProcess(Process process) {
        this.process = process;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public boolean isProtocol() {
        return protocol;
    }
    public void setProtocol(boolean protocol) {
        this.protocol = protocol;
    }

    public enum Type {
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
            for(Type type0 : Type.values()) {
                if(type0.name().equalsIgnoreCase(type)) {
                    return type0;
                }
            }
            return null;
        }
    }

    public enum Process {
        EQUATION,
        OPTION;

        public static Process get(String process) {
            for(Process process0 : Process.values()) {
                if(process0.name().equalsIgnoreCase(process)) {
                    return process0;
                }
            }
            return null;
        }
    }
}