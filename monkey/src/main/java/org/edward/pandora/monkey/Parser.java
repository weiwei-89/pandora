package org.edward.pandora.monkey;

import org.edward.pandora.monkey.exception.ParseException;
import org.edward.pandora.monkey.function.InfixParseFunction;
import org.edward.pandora.monkey.function.PrefixParseFunction;
import org.edward.pandora.monkey.model.*;
import org.edward.pandora.monkey.model.expression.*;
import org.edward.pandora.monkey.model.statement.BlockStatement;
import org.edward.pandora.monkey.model.statement.ExpressionStatement;
import org.edward.pandora.monkey.model.statement.LetStatement;
import org.edward.pandora.monkey.model.statement.ReturnStatement;

import java.util.*;

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
        this.prefixParseFunctionMap.put(Token.Type.BOOLEAN, this::parseBoolean);
        this.prefixParseFunctionMap.put(Token.Type.BANG, this::parsePrefixExpression);
        this.prefixParseFunctionMap.put(Token.Type.PLUS, this::parsePrefixExpression);
        this.prefixParseFunctionMap.put(Token.Type.MINUS, this::parsePrefixExpression);
        this.prefixParseFunctionMap.put(Token.Type.LPAREN, this::parseGroupedExpression);
        this.prefixParseFunctionMap.put(Token.Type.IF, this::parseIfExpression);
        this.prefixParseFunctionMap.put(Token.Type.FUNCTION, this::parseFunctionLiteral);
        this.infixParseFunctionMap.put(Token.Type.EQ, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.NOT_EQ, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.LT, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.GT, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.PLUS, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.MINUS, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.ASTERISK, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.SLASH, this::parseInfixExpression);
        this.infixParseFunctionMap.put(Token.Type.LPAREN, this::parseCallExpression);
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

    public enum TokenPriority {
        EQ(Token.Type.EQ, Priority.EQUALS),
        NOT_EQ(Token.Type.NOT_EQ, Priority.EQUALS),
        LT(Token.Type.LT, Priority.LESSGREATER),
        GT(Token.Type.GT, Priority.LESSGREATER),
        PLUS(Token.Type.PLUS, Priority.SUM),
        MINUS(Token.Type.MINUS, Priority.SUM),
        ASTERISK(Token.Type.ASTERISK, Priority.PRODUCT),
        SLASH(Token.Type.SLASH, Priority.PRODUCT),
        LPAREN(Token.Type.LPAREN, Priority.CALL);

        private final Token.Type tokenType;
        private final Priority priority;

        TokenPriority(Token.Type tokenType, Priority priority) {
            this.tokenType = tokenType;
            this.priority = priority;
        }

        public Token.Type getTokenType() {
            return tokenType;
        }
        public Priority getPriority() {
            return priority;
        }

        public static TokenPriority get(Token.Type tokenType) {
            for(TokenPriority tokenPriority : TokenPriority.values()) {
                if(tokenPriority.getTokenType() == tokenType) {
                    return tokenPriority;
                }
            }
            return null;
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
        LetStatement letStatement = new LetStatement(this.currentToken);
        this.expect(Token.Type.IDENT);
        this.go();
        IdentifierExpression identifierExpression = new IdentifierExpression(this.currentToken);
        identifierExpression.setValue(this.currentToken.getLiteral());
        letStatement.setName(identifierExpression);
        this.expect(Token.Type.ASSIGN);
        this.go();
        this.go();
        letStatement.setValue(this.parseExpression(Priority.LOWEST.getValue()));
        return letStatement;
    }

    private ReturnStatement parseReturnStatement() throws Exception {
        ReturnStatement returnStatement = new ReturnStatement(this.currentToken);
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
        while(true) {
            if(this.nextToken.getType() == Token.Type.SEMICOLON) {
                break;
            }
            TokenPriority nextTokenPriority = TokenPriority.get(this.nextToken.getType());
            if(nextTokenPriority == null) {
                break;
            }
            if(priority >= nextTokenPriority.getPriority().getValue()) {
                break;
            }
            InfixParseFunction infixParseFunction = this.infixParseFunctionMap.get(this.nextToken.getType());
            if(infixParseFunction == null) {
                throw new ParseException(
                        String.format("there is no infix parsing function for \"%s\"", this.nextToken.getLiteral()));
            }
            this.go();
            leftExpression = infixParseFunction.apply(leftExpression);
        }
        return leftExpression;
    }

    private Expression parseIdentifier() throws Exception {
        IdentifierExpression identifierExpression = new IdentifierExpression(this.currentToken);
        identifierExpression.setValue(this.currentToken.getLiteral());
        return identifierExpression;
    }

    private Expression parseIntegerLiteral() throws Exception {
        IntegerExpression integerExpression = new IntegerExpression(this.currentToken);
        integerExpression.setValue(Integer.parseInt(this.currentToken.getLiteral()));
        return integerExpression;
    }

    private Expression parseBoolean() throws Exception {
        BooleanExpression booleanExpression = new BooleanExpression(this.currentToken);
        booleanExpression.setValue(Boolean.parseBoolean(this.currentToken.getLiteral()));
        return booleanExpression;
    }

    private Expression parsePrefixExpression() throws Exception {
        PrefixExpression prefixExpression = new PrefixExpression(this.currentToken);
        prefixExpression.setOperator(this.currentToken.getLiteral());
        this.go();
        prefixExpression.setRight(this.parseExpression(Priority.PREFIX.getValue()));
        return prefixExpression;
    }

    private Expression parseInfixExpression(Expression left) throws Exception {
        InfixExpression infixExpression = new InfixExpression(this.currentToken);
        infixExpression.setLeft(left);
        infixExpression.setOperator(this.currentToken.getLiteral());
        TokenPriority currentTokenPriority = TokenPriority.get(this.currentToken.getType());
        this.go();
        infixExpression.setRight(this.parseExpression(currentTokenPriority.getPriority().getValue()));
        return infixExpression;
    }

    private Expression parseGroupedExpression() throws Exception {
        this.go();
        Expression expression = this.parseExpression(Priority.LOWEST.getValue());
        this.expect(Token.Type.RPAREN);
        this.go();
        return expression;
    }

    private Expression parseIfExpression() throws Exception {
        IfExpression ifExpression = new IfExpression(this.currentToken);
        this.expect(Token.Type.LPAREN);
        this.go();
        this.go();
        ifExpression.setCondition(this.parseExpression(Priority.LOWEST.getValue()));
        this.expect(Token.Type.RPAREN);
        this.go();
        ifExpression.setConsequence(this.parseBlockStatement());
        if(this.nextToken.getType() == Token.Type.ELSE) {
            this.go();
            ifExpression.setAlternative(this.parseBlockStatement());
        }
        return ifExpression;
    }

    private BlockStatement parseBlockStatement() throws Exception {
        this.expect(Token.Type.LBRACE);
        this.go();
        BlockStatement blockStatement = new BlockStatement(this.currentToken);
        while(true) {
            this.go();
            if(this.currentToken.getType() == Token.Type.SEMICOLON) {
                continue;
            }
            if(this.currentToken.getType() == Token.Type.RBRACE) {
                break;
            }
            Statement statement = this.parseStatement();
            if(statement == null) {
                continue;
            }
            blockStatement.addStatement(statement);
        }
        return blockStatement;
    }

    private Expression parseFunctionLiteral() throws Exception {
        FunctionLiteralExpression functionLiteralExpression = new FunctionLiteralExpression(this.currentToken);
        functionLiteralExpression.addParameters(this.parseFunctionParameters());
        functionLiteralExpression.setBody(this.parseBlockStatement());
        return functionLiteralExpression;
    }

    private List<IdentifierExpression> parseFunctionParameters() throws Exception {
        this.expect(Token.Type.LPAREN);
        this.go();
        if(this.nextToken.getType() == Token.Type.RPAREN) {
            this.go();
            return Collections.emptyList();
        }
        List<IdentifierExpression> identifierExpressionList = new ArrayList<>();
        while(true) {
            if(this.nextToken.getType() == Token.Type.RPAREN) {
                this.go();
                break;
            }
            if(this.nextToken.getType() == Token.Type.COMMA) {
                this.go();
                continue;
            }
            if(this.nextToken.getType() == Token.Type.IDENT) {
                this.go();
                IdentifierExpression identifierExpression = (IdentifierExpression) this.parseIdentifier();
                identifierExpressionList.add(identifierExpression);
            }
        }
        return identifierExpressionList;
    }

    private Expression parseCallExpression(Expression function) throws Exception {
        CallExpression callExpression = new CallExpression(this.currentToken, function);
        callExpression.addArguments(this.parseCallArguments());
        return callExpression;
    }

    private List<Expression> parseCallArguments() throws Exception {
        if(this.nextToken.getType() == Token.Type.RPAREN) {
            this.go();
            return Collections.emptyList();
        }
        List<Expression> expressionList = new ArrayList<>();
        while(true) {
            if(this.nextToken.getType() == Token.Type.RPAREN) {
                this.go();
                break;
            }
            if(this.nextToken.getType() == Token.Type.COMMA) {
                this.go();
                continue;
            }
            this.go();
            expressionList.add(this.parseExpression(Priority.LOWEST.getValue()));
        }
        return expressionList;
    }
}