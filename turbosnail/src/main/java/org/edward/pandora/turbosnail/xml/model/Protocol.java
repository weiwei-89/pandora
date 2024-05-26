package org.edward.pandora.turbosnail.xml.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Protocol extends Element {
    private List<Segment> segmentList;
    private Map<String, String> cache = new HashMap<>();

    public List<Segment> getSegmentList() {
        return this.segmentList;
    }
    public void setSegmentList(List<Segment> segmentList) {
        this.segmentList = segmentList;
    }
    public Map<String, String> getCache() {
        return cache;
    }
}