package org.edward.pandora.monkey.model.expression;

import org.edward.pandora.monkey.model.Expression;
import org.edward.pandora.monkey.model.Token;
import org.edward.pandora.monkey.model.statement.BlockStatement;

public class IfExpression implements Expression {
    private final Token token;

    public IfExpression(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    private Expression condition;
    private BlockStatement consequence;
    private BlockStatement alternative;

    public Expression getCondition() {
        return condition;
    }
    public void setCondition(Expression condition) {
        this.condition = condition;
    }
    public BlockStatement getConsequence() {
        return consequence;
    }
    public void setConsequence(BlockStatement consequence) {
        this.consequence = consequence;
    }
    public BlockStatement getAlternative() {
        return alternative;
    }
    public void setAlternative(BlockStatement alternative) {
        this.alternative = alternative;
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
        sb.append("if");
        if(this.condition instanceof InfixExpression) {
            sb.append("(");
            sb.append(this.condition.string());
            sb.append(")");
            sb.append(" ");
        } else {
            sb.append(" ");
            sb.append(this.condition.string());
            sb.append(" ");
        }
        sb.append("{");
        sb.append("\n");
        sb.append(this.consequence.string());
        sb.append("}");
        if(this.alternative != null) {
            sb.append(" ");
            sb.append("else");
            sb.append(" ");
            sb.append("{");
            sb.append("\n");
            sb.append(this.alternative.string());
            sb.append("}");
        }
        return sb.toString();
    }
}