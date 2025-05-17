package org.edward.pandora.turbosnail.xml.model;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

public class Position extends Element {
    private int length;
    private boolean variableLength = false;
    private String lengthFormula;
    private String[] operators;
    private JexlExpression jexlExpression;

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
    public String[] getOperators() {
        return this.operators;
    }
    public void setOperators(String[] operators) {
        this.operators = operators;
    }
    public JexlExpression getJexlExpression() {
        return this.jexlExpression;
    }
    public void setJexlExpression(JexlExpression jexlExpression) {
        this.jexlExpression = jexlExpression;
    }

    public void analyzeLength() throws Exception {
        if(this.variableLength) {
            JexlContext jexlContext = new MapContext();
            Protocol protocol = (Protocol) this.getProtocol();
            for(int i=0; i<this.operators.length; i++) {
                String operator = this.operators[i];
                String operatorCode = this.convertUniqueCode(operator);
                if(protocol.getCache().containsKey(operatorCode)) {
                    String operatorValue = protocol.getCache().get(operatorCode);
                    jexlContext.set(operator, operatorValue);
                } else {
                    throw new Exception(String.format("finding operator value failed [operator:%s]", operator));
                }
            }
            Object result = this.jexlExpression.evaluate(jexlContext);
            this.length = Integer.parseInt(result.toString());
        }
        if(this.length < 0) {
            throw new Exception("\"length\" is less than 0");
        }
    }
}