package org.edward.pandora.turbosnail.xml.model;

public class Position extends Element {
    private int length;
    private boolean variableLength;
    private String lengthFormula;

    public int getLength() {
        return this.length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public boolean isVariableLength() {
        return this.variableLength;
    }
    public void setVariableLength(boolean variableLength) {
        this.variableLength = variableLength;
    }
    public String getLengthFormula() {
        return this.lengthFormula;
    }
    public void setLengthFormula(String lengthFormula) {
        this.lengthFormula = lengthFormula;
    }
}