package org.edward.pandora.turbosnail.xml.model;

public class Element {
    private Element protocol;
    private Element parent;
    private String id;
    private String name;
    private String description;

    public Element getProtocol() {
        return this.protocol;
    }
    public void setProtocol(Element protocol) {
        this.protocol = protocol;
    }
    public Element getParent() {
        return this.parent;
    }
    public void setParent(Element parent) {
        this.parent = parent;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}