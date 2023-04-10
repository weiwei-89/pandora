package org.edward.pandora.turbosnail;

import org.edward.pandora.turbosnail.xml.model.Protocol;

import java.util.HashMap;

public class Papers extends HashMap<String, Protocol> {
    public Papers() {

    }

    public Papers(int size) {
        super(size);
    }
}