package org.edward.pandora.monkey.model;

public class ReturnStatement implements Statement {
    private final Token token = new Token(Token.Keywords.RETURN.getLiteral(), Token.Keywords.RETURN.getType());

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
        sb.append(this.tokenLiteral());
        sb.append(" ");
        sb.append(this.value.string());
        return sb.toString();
    }
}