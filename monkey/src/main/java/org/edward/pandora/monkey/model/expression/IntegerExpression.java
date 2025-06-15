package org.edward.pandora.monkey.model.expression;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Token;

public class IntegerExpression implements Expression {
    private final Token token;

    public IntegerExpression(Token token) {
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