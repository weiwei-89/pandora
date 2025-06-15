package org.edward.pandora.monkey.model.statement;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.expression.IdentifierExpression;
import org.edward.pandora.monkey.model.Statement;
import org.edward.pandora.monkey.model.Token;

public class LetStatement implements Statement {
    private final Token token;

    public LetStatement(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private IdentifierExpression name;
    private Expression value;

    public IdentifierExpression getName() {
        return name;
    }
    public void setName(IdentifierExpression name) {
        this.name = name;
    }
    public Expression getValue() {
        return value;
    }
    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String tokenLiteral() {
        return this.token.getLiteral();
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        sb.append("let");
        sb.append(" ");
        sb.append(this.name.string());
        sb.append(" ");
        sb.append("=");
        sb.append(" ");
        sb.append(this.value.string());
        return sb.toString();
    }
}