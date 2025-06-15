package org.edward.pandora.monkey.model.expression;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Token;

public class BooleanExpression implements Expression {
    private final Token token;

    public BooleanExpression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private boolean value;

    public boolean isValue() {
        return value;
    }
    public void setValue(boolean value) {
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