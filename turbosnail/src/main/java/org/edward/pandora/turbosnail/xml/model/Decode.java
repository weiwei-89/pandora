package org.edward.pandora.turbosnail.xml.model;

import java.util.List;

public class Decode extends Element {
    private Type type;
    private String protocolId;
    private String foreignProtocolId;
    private List<Option> optionList;

    public Type getType() {
        return this.type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public String getProtocolId() {
        return this.protocolId;
    }
    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }
    public String getForeignProtocolId() {
        return this.foreignProtocolId;
    }
    public void setForeignProtocolId(String foreignProtocolId) {
        this.foreignProtocolId = foreignProtocolId;
    }
    public List<Option> getOptionList() {
        return this.optionList;
    }
    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
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
        DOUBLE_LE,
        OPTION,
        PROTOCOL;

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