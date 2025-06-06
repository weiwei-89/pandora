package org.edward.pandora.monkey.model;

import java.util.ArrayList;
import java.util.List;

public class Program implements Node {
    private final List<Statement> statementList;

    public Program() {
        this.statementList = new ArrayList<>();
    }

    public void addStatement(Statement statement) {
        this.statementList.add(statement);
    }

    @Override
    public String tokenLiteral() {
        if(this.statementList.size() == 0) {
            return "";
        }
        return this.statementList.get(0).tokenLiteral();
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