package org.edward.pandora.monkey.model.statement;

import org.edward.pandora.monkey.model.Statement;
import org.edward.pandora.monkey.model.Token;

import java.util.ArrayList;
import java.util.List;

public class BlockStatement implements Statement {
    private final Token token;
    private final List<Statement> statementList;

    public BlockStatement(Token token) {
        this.token = token;
        this.statementList = new ArrayList<>();
    }

    public Token getToken() {
        return token;
    }

    public void addStatement(Statement statement) {
        this.statementList.add(statement);
    }

    public List<Statement> getStatementList() {
        return this.statementList;
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
        if(this.statementList.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(Statement statement : this.statementList) {
            sb.append(statement.string()).append("\n");
        }
        return sb.toString();
    }
}