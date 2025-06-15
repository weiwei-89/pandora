package org.edward.pandora.monkey.model.expression;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Token;

public class InfixExpression implements Expression {
    private final Token token;

    public InfixExpression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private Expression left;
    private String operator;
    private Expression right;

    public Expression getLeft() {
        return left;
    }
    public void setLeft(Expression left) {
        this.left = left;
    }
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
        if(this.left instanceof InfixExpression) {
            sb.append("(");
            sb.append(this.left.string());
            sb.append(")");
        } else {
            sb.append(this.left.string());
        }
        sb.append(this.operator);
        if(this.right instanceof InfixExpression) {
            sb.append("(");
            sb.append(this.right.string());
            sb.append(")");
        } else {
            sb.append(this.right.string());
        }
        return sb.toString();
    }
}