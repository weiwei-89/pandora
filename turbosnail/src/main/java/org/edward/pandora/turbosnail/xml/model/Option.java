package org.edward.pandora.turbosnail.xml.model;

public class Option extends Element {
    private String code;
    private String value;
    private boolean range;
    private int min;
    private int max;
    private String unit;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public boolean isRange() {
        return this.range;
    }
    public void setRange(boolean range) {
        this.range = range;
    }
    public int getMin() {
        return this.min;
    }
    public void setMin(int min) {
        this.min = min;
    }
    public int getMax() {
        return this.max;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public String getUnit() {
        return this.unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}