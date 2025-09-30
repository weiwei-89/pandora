package org.edward.pandora.monkey.model.evaluator;

import org.edward.pandora.monkey.model.Element;
import org.edward.pandora.monkey.model.Environment;
import org.edward.pandora.monkey.model.expression.IdentifierExpression;
import org.edward.pandora.monkey.model.statement.BlockStatement;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionElement implements Element {
    private final List<IdentifierExpression> parameters;
    private final BlockStatement body;
    private final Environment env;

    public FunctionElement(
            List<IdentifierExpression> parameters,
            BlockStatement body,
            Environment env
    ) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    public List<IdentifierExpression> getParameters() {
        return this.parameters;
    }

    public BlockStatement getBody() {
        return this.body;
    }

    public Environment getEnv() {
        return this.env;
    }

    @Override
    public Type type() {
        return Type.FUNCTION;
    }

    @Override
    public String inspect() {
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