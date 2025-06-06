package org.edward.pandora.monkey.model;

public class IntegerLiteral implements Expression {
    private final Token token;

    public IntegerLiteral(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private int value;

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return this.token.getLiteral();
    }

    @Override
    public String string() {
        return String.valueOf(this.value);
    }
}