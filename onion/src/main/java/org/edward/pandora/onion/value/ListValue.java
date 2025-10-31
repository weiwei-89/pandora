package org.edward.pandora.onion.value;

import java.util.ArrayList;
import java.util.List;

public class ListValue implements Value {
    private final List<Value> list;

    public ListValue() {
        this.list = new ArrayList<>();
    }

    public ListValue(int size) {
        this.list = new ArrayList<>(size);
    }

    @Override
    public Type type() {
        return Type.LIST;
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0; i<this.list.size(); i++) {
            Value value = this.list.get(i);
            sb.append(value.inspect());
            if(i+1 < this.list.size()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void add(Value value) {
        this.list.add(value);
    }

    public Value get(int index) {
        return this.list.get(index);
    }
}