package org.edward.pandora.turbosnail.xml.model;

public class Segment extends Element {
    private String value;
    private Position position;
    private Decode decode;
    private boolean skip = false;

    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Position getPosition() {
        return this.position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public Decode getDecode() {
        return this.decode;
    }
    public void setDecode(Decode decode) {
        this.decode = decode;
    }
    public boolean isSkip() {
        return this.skip;
    }
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}