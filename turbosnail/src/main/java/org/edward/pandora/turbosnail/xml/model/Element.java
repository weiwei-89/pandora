package org.edward.pandora.turbosnail.xml.model;

import org.edward.pandora.turbosnail.xml.model.property.Property;

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

    public String generateUniqueCode() {
        return generateUniqueCode(this.getProtocol().getId(), this.getId());
    }

    public static String generateUniqueCode(Element element) {
        return generateUniqueCode(element.getProtocol().getId(), element.getId());
    }

    private static String generateUniqueCode(String protocolId, String elementId) {
        return String.format("%s%s%s", protocolId, Property.SEPARATOR, elementId);
    }

    public String convertUniqueCode(String code) {
        if(code.contains(Property.SEPARATOR)) {
            return code;
        }
        return generateUniqueCode(this.getProtocol().getId(), code);
    }

    public void copy(Element element) {
        element.protocol = this.protocol;
        element.parent = this.parent;
        element.id = this.id;
        element.name = this.name;
        element.description = this.description;
    }
}