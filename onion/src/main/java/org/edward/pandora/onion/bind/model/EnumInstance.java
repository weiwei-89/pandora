package org.edward.pandora.onion.bind.model;

import java.util.HashMap;

public class EnumInstance extends HashMap<String, Enum> {
    public EnumInstance() {

    }

    public EnumInstance(int size) {
        super(size);
    }
}