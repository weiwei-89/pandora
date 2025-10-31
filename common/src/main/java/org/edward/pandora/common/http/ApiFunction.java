package org.edward.pandora.common.http;

import java.lang.reflect.Method;

public class ApiFunction {
    private final String path;
    private final Method instance;
    private final ApiEntrance entrance;

    public ApiFunction(String path, Method instance, ApiEntrance entrance) {
        this.path = path;
        this.instance = instance;
        this.entrance = entrance;
    }

    public String getPath() {
        return path;
    }
    public Method getInstance() {
        return instance;
    }
    public ApiEntrance getEntrance() {
        return entrance;
    }
}