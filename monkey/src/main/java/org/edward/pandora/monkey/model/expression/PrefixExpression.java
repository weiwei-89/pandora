package org.edward.pandora.monkey.model.expression;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Token;

public class PrefixExpression implements Expression {
    private final Token token;

    public PrefixExpression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private String operator;
    private Expression right;

    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Expression getRight() {
        return right;
    }
    public void setRight(Expression right) {
        this.right = right;
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
        StringBuilder sb = new StringBuilder();
        sb.append(this.operator);
        sb.append(this.right.string());
        return sb.toString();
    }
}