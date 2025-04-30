package org.edward.pandora.turbosnail.xml.model;

import java.util.List;

public class Multi extends Element {
    private List<Segment> segmentList;
    private boolean enable;
    private int count;

    public List<Segment> getSegmentList() {
        return this.segmentList;
    }
    public void setSegmentList(List<Segment> segmentList) {
        this.segmentList = segmentList;
    }
    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}