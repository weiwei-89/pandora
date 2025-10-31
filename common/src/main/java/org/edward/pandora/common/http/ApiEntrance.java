package org.edward.pandora.common.http;

public class ApiEntrance {
    private final String path;
    private final Object instance;

    public ApiEntrance(String path, Object instance) {
        this.path = path;
        this.instance = instance;
    }

    public String getPath() {
        return path;
    }
    public Object getInstance() {
        return instance;
    }
}