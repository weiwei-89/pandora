package org.edward.pandora.turbosnail;

public class Path {
    private final String path;
    private final String protocolId;

    public Path(String path, String protocolId) {
        this.path = path;
        this.protocolId = protocolId;
    }

    public String getPath() {
        return this.path;
    }
    public String getProtocolId() {
        return this.protocolId;
    }
}