package org.edward.pandora.monkey.model.expression;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallExpression implements Expression {
    private final Token token;
    private final Expression function;
    private final List<Expression> arguments;

    public CallExpression(Token token, Expression function) {
        this.token = token;
        this.function = function;
        this.arguments = new ArrayList<>();
    }

    public Token getToken() {
        return token;
    }

    public Expression getFunction() {
        return this.function;
    }

    public List<Expression> getArguments() {
        return this.arguments;
    }

    public void addArguments(List<Expression> arguments) {
        this.arguments.addAll(arguments);
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
        sb.append(this.function.string());
        sb.append("(");
        sb.append(this.arguments.stream()
                .map(t->t.string())
                .collect(Collectors.joining(",")));
        sb.append(")");
        return sb.toString();
    }
}