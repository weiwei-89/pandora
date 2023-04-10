package org.edward.pandora.turbosnail.xml.model;

import java.util.List;

public class Protocol extends Element {
    private List<Segment> segmentList;

    public List<Segment> getSegmentList() {
        return this.segmentList;
    }
    public void setSegmentList(List<Segment> segmentList) {
        this.segmentList = segmentList;
    }
}