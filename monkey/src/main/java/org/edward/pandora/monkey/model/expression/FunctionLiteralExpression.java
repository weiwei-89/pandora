package org.edward.pandora.monkey.model.expression;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Token;
import org.edward.pandora.monkey.model.statement.BlockStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionLiteralExpression implements Expression {
    private final Token token;
    private final List<IdentifierExpression> parameters;

    public FunctionLiteralExpression(Token token) {
        this.token = token;
        this.parameters = new ArrayList<>();
    }

    public Token getToken() {
        return token;
    }

    public List<IdentifierExpression> getParameters() {
        return this.parameters;
    }

    public void addParameter(IdentifierExpression identifier) {
        this.parameters.add(identifier);
    }

    public void addParameters(List<IdentifierExpression> parameters) {
        this.parameters.addAll(parameters);
    }

    private BlockStatement body;

    public BlockStatement getBody() {
        return body;
    }
    public void setBody(BlockStatement body) {
        this.body = body;
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
        sb.append("fn");
        sb.append("(");
        sb.append(this.parameters.stream()
                .map(t->t.string())
                .collect(Collectors.joining(",")));
        sb.append(")");
        sb.append(" ");
        sb.append("{");
        sb.append("\n");
        sb.append(this.body.string());
        sb.append("}");
        return sb.toString();
    }
}