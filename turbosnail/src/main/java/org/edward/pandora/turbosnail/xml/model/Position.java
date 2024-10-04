package org.edward.pandora.turbosnail.xml.model;

import org.apache.commons.jexl3.*;
import org.edward.pandora.turbosnail.xml.model.property.Property;

public class Position extends Element {
    private int length;
    private boolean variableLength = false;
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

    public void analyzeLength() throws Exception {
        if(this.variableLength) {
            JexlContext jexlContext = new MapContext();
            String[] operators = Property.extractOperators(this.lengthFormula);
            Protocol protocol = (Protocol) this.getParent().getParent();
            for(int i=0; i<operators.length; i++) {
                String operator = operators[i];
                if(protocol.getCache().containsKey(operator)) {
                    String operatorValue = protocol.getCache().get(operator);
                    jexlContext.set(operator, operatorValue);
                }
            }
            JexlEngine jexlEngine = new JexlBuilder().create();
            JexlExpression jexlExpression = jexlEngine.createExpression(this.lengthFormula);
            Object result = jexlExpression.evaluate(jexlContext);
            int length = Integer.valueOf(result.toString());
            this.length = length;
        }
        if(this.length < 0) {
            throw new Exception("\"length\" is less than 0");
        }
    }
}