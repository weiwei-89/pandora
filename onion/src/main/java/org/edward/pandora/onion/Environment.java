package org.edward.pandora.onion;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private final List<Class<?>> clazzList;

    public Environment() {
        this.clazzList = new ArrayList<>();
    }

    public void addClazz(Class<?> clazz) {
        this.clazzList.add(clazz);
    }

    public List<Class<?>> getClazzList() {
        return this.clazzList;
    }
}