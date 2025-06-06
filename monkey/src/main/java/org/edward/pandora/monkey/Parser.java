package org.edward.pandora.monkey;

import org.edward.pandora.monkey.exception.ParseException;
import org.edward.pandora.monkey.function.InfixParseFunction;
import org.edward.pandora.monkey.function.PrefixParseFunction;
import org.edward.pandora.monkey.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final Lexer lexer;
    private final Map<Token.Type, PrefixParseFunction> prefixParseFunctionMap = new HashMap<>();
    private final Map<Token.Type, InfixParseFunction> infixParseFunctionMap = new HashMap<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.registerFunction();
    }

    private void registerFunction() {
        this.prefixParseFunctionMap.put(Token.Type.IDENT, this::parseIdentifier);
        this.prefixParseFunctionMap.put(Token.Type.INT, this::parseIntegerLiteral);
    }

    public enum Priority {
        _,
        LOWEST,
        EQUALS,
        LESSGREATER,
        SUM,
        PRODUCT,
        PREFIX,
        CALL;

        public int getValue() {
            return this.ordinal();
        }
    }

    private Token currentToken;
    private Token nextToken;

    private void go() throws Exception {
        this.currentToken = this.nextToken;
        this.nextToken = this.lexer.nextToken();
    }

    private void expect(Token.Type tokenType) throws Exception {
        if(this.nextToken.getType() == tokenType) {
            return;
        }
        throw new ParseException(String.format("expect token \"%s\"", tokenType.getCode()));
    }

    public Program parseProgram() throws Exception {
        this.go();
        Program program = new Program();
        while(true) {
            this.go();
            if(this.currentToken.getType() == Token.Type.SEMICOLON) {
                continue;
            }
            if(this.currentToken.getType() == Token.Type.EOF) {
                break;
            }
            Statement statement = this.parseStatement();
            if(statement == null) {
                continue;
            }
            program.addStatement(statement);
        }
        return program;
    }

    private Statement parseStatement() throws Exception {
        switch(this.currentToken.getType()) {
            case LET:
                return this.parseLetStatement();
            case RETURN:
                return this.parseReturnStatement();
            default:
                return this.parseExpressionStatement();
        }
    }

    private LetStatement parseLetStatement() throws Exception {
        LetStatement letStatement = new LetStatement();
        this.expect(Token.Type.IDENT);
        this.go();
        Identifier identifier = new Identifier(this.currentToken);
        identifier.setValue(this.currentToken.getLiteral());
        letStatement.setName(identifier);
        this.expect(Token.Type.ASSIGN);
        this.go();
        this.go();
        letStatement.setValue(this.parseExpression(Priority.LOWEST.getValue()));
        return letStatement;
    }

    private ReturnStatement parseReturnStatement() throws Exception {
        ReturnStatement returnStatement = new ReturnStatement();
        this.go();
        returnStatement.setValue(this.parseExpression(Priority.LOWEST.getValue()));
        return returnStatement;
    }

    private ExpressionStatement parseExpressionStatement() throws Exception {
        ExpressionStatement expressionStatement = new ExpressionStatement(this.currentToken);
        expressionStatement.setExpression(this.parseExpression(Priority.LOWEST.getValue()));
        return expressionStatement;
    }

    private Expression parseExpression(int priority) throws Exception {
        PrefixParseFunction prefixParseFunction = this.prefixParseFunctionMap.get(this.currentToken.getType());
        if(prefixParseFunction == null) {
            throw new ParseException(
                    String.format("there is no prefix parsing function for \"%s\"", this.currentToken.getLiteral()));
        }
        Expression leftExpression = prefixParseFunction.apply();
        return leftExpression;
    }

    private Expression parseIdentifier() throws Exception {
        Identifier identifier = new Identifier(this.currentToken);
        identifier.setValue(this.currentToken.getLiteral());
        return identifier;
    }

    private Expression parseIntegerLiteral() throws Exception {
        IntegerLiteral integerLiteral = new IntegerLiteral(this.currentToken);
        integerLiteral.setValue(Integer.parseInt(this.currentToken.getLiteral()));
        return integerLiteral;
    }
}