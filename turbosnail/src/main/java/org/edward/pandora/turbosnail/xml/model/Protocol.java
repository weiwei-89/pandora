package org.edward.pandora.turbosnail.xml.model;

import java.util.*;

public class Protocol extends Element {
    private List<Element> elementList;
    private Set<String> cacheSet = new HashSet<>();
    private Map<String, String> cache = new HashMap<>();

    public List<Element> getElementList() {
        return elementList;
    }
    public void setElementList(List<Element> elementList) {
        this.elementList = elementList;
    }
    public Set<String> getCacheSet() {
        return cacheSet;
    }
    public Map<String, String> getCache() {
        return cache;
    }
}