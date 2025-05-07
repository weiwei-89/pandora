package org.edward.pandora.turbosnail.xml.model;

import java.util.List;

public class Segment extends Element {
    private Position position;
    private Decode decode;
    private Options options;
    private List<Option> optionList;
    private boolean skip = false;
    private boolean multi = false;
    private int count;

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
    public boolean isMulti() {
        return multi;
    }
    public void setMulti(boolean multi) {
        this.multi = multi;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public String generateUniqueCode() {
        return generateUniqueCode(this.getProtocol().getId(), this.getId());
    }

    public static String generateUniqueCode(Segment segment) {
        return generateUniqueCode(segment.getProtocol().getId(), segment.getId());
    }

    public static String generateUniqueCode(String protocolId, String segmentId) {
        return String.format("%s.%s", protocolId, segmentId);
    }

    public void copy(Segment segment) {
        super.copy(segment);
        segment.position = this.position;
        segment.decode = this.decode;
        segment.options = this.options;
        segment.optionList = this.optionList;
        segment.skip = this.skip;
        segment.multi = this.multi;
        segment.count = this.count;
    }
}