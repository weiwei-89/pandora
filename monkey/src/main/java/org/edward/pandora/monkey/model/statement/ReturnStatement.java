package org.edward.pandora.monkey.model.statement;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Statement;
import org.edward.pandora.monkey.model.Token;

public class ReturnStatement implements Statement {
    private final Token token;

    public ReturnStatement(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private Expression value;

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
        sb.append("return");
        sb.append(" ");
        sb.append(this.value.string());
        return sb.toString();
    }
}