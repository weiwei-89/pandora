package org.edward.pandora.monkey.model;

public class LetStatement implements Statement {
    private final Token token = new Token(Token.Keywords.LET.getLiteral(), Token.Keywords.LET.getType());

    public Token getToken() {
        return token;
    }

    private Identifier name;
    private Expression value;

    public Identifier getName() {
        return name;
    }
    public void setName(Identifier name) {
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
        sb.append(this.tokenLiteral());
        sb.append(" ");
        sb.append(this.name.string());
        sb.append(" = ");
        sb.append(this.value.string());
        return sb.toString();
    }
}