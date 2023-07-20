package org.edward.pandora.turbosnail.xml.model;

public class Options extends Element {
    private Decode.Type type;

    public Decode.Type getType() {
        return this.type;
    }
    public void setType(Decode.Type type) {
        this.type = type;
    }
}