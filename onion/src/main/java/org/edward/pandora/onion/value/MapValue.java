package org.edward.pandora.onion.value;

import java.util.HashMap;
import java.util.Map;

public class MapValue implements Value {
    private final Map<String, Value> map;

    public MapValue() {
        this.map = new HashMap<>();
    }

    public MapValue(int size) {
        this.map = new HashMap<>(size);
    }

    @Override
    public Type type() {
        return Type.MAP;
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for(Map.Entry<String, Value> entry : this.map.entrySet()) {
            i++;
            String key = entry.getKey();
            sb.append(key);
            sb.append(":");
            Value value = entry.getValue();
            sb.append(value.inspect());
            if(i < this.map.size()) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public void set(String key, Value value) {
        this.map.put(key, value);
    }

    public Value get(String key) {
        return this.map.get(key);
    }
}