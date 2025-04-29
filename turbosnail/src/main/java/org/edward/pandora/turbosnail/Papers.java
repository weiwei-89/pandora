package org.edward.pandora.turbosnail;

import org.edward.pandora.turbosnail.xml.model.Protocol;

import java.util.HashMap;

public class Papers extends HashMap<String, Protocol> {
    private final String protocolId;

    public Papers(int size, String protocolId) {
        super(size);
        this.protocolId = protocolId;
    }

    public String getProtocolId() {
        return this.protocolId;
    }
}