package org.edward.pandora.turbosnail.xml.model;

import java.util.List;

public class Segment extends Element {
    private Position position;
    private Decode decode;
    private Options options;
    private List<Option> optionList;
    private boolean skip = false;

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
    public Options getOptions() {
        return options;
    }
    public void setOptions(Options options) {
        this.options = options;
    }
    public List<Option> getOptionList() {
        return this.optionList;
    }
    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }
    public boolean isSkip() {
        return this.skip;
    }
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}