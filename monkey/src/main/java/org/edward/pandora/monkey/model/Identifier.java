package org.edward.pandora.monkey.model;

public class Identifier implements Expression {
    private final Token token;

    public Identifier(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private String value;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
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
        return this.value;
    }
}