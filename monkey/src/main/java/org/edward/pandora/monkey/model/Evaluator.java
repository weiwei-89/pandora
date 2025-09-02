package org.edward.pandora.monkey.model;

import org.edward.pandora.monkey.model.evaluator.BooleanElement;
import org.edward.pandora.monkey.model.evaluator.EvalFunction;
import org.edward.pandora.monkey.model.evaluator.IntegerElement;
import org.edward.pandora.monkey.model.expression.BooleanExpression;
import org.edward.pandora.monkey.model.expression.InfixExpression;
import org.edward.pandora.monkey.model.expression.IntegerExpression;
import org.edward.pandora.monkey.model.expression.PrefixExpression;
import org.edward.pandora.monkey.model.statement.ExpressionStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
    private final Map<Class<? extends Node>, EvalFunction<Node, Element>> handlers = new HashMap<>();

    public Evaluator() {
        this.registerHandlers();
    }

    private void registerHandlers() {
        this.register(IntegerExpression.class, this::handleIntegerExpression);
        this.register(BooleanExpression.class, this::handleBooleanExpression);
        this.register(Program.class, this::handleProgram);
        this.register(ExpressionStatement.class, this::handleExpressionStatement);
        this.register(PrefixExpression.class, this::handlePrefixExpression);
        this.register(InfixExpression.class, this::handleInfixExpression);
    }

    private <T extends Node> void register(Class<T> nodeType, EvalFunction<T, Element> handler) {
        this.handlers.put(
                nodeType,
                node -> {
                    return handler.apply(nodeType.cast(node));
                }
        );
    }

    public Element eval(Node node) throws Exception {
        EvalFunction<Node, Element> handler = this.handlers.get(node.getClass());
        if(handler == null) {
            throw new Exception(String.format("unsupported expression:\n%s", node.string()));
        }
        return handler.apply(node);
    }

    private Element handleIntegerExpression(IntegerExpression expression) throws Exception {
        return new IntegerElement(expression.getValue());
    }

    private Element handleBooleanExpression(BooleanExpression expression) throws Exception {
        return BooleanElement.of(expression.isValue());
    }

    private Element handleProgram(Program program) throws Exception {
        List<Statement> statementList = program.getStatementList();
        if(statementList==null || statementList.size()==0) {
            return null;
        }
        Element result = null;
        for(Statement statement : statementList) {
            result = this.eval(statement);
        }
        return result;
    }

    private Element handleExpressionStatement(ExpressionStatement statement) throws Exception {
        return this.eval(statement.getExpression());
    }

    private Element handlePrefixExpression(PrefixExpression expression) throws Exception {
        String operator = expression.getOperator();
        Element rightResult = this.eval(expression.getRight());
        if("!".equals(operator)) {
            if(rightResult.type() == Element.Type.BOOLEAN) {
                if(rightResult == BooleanElement.TRUE) {
                    return BooleanElement.FALSE;
                } else if(rightResult == BooleanElement.FALSE) {
                    return BooleanElement.TRUE;
                }
            } else if(rightResult.type() == Element.Type.INTEGER) {
                IntegerElement rightIntegerElement = (IntegerElement) rightResult;
                if(rightIntegerElement.getValue() > 0) {
                    return BooleanElement.FALSE;
                }
                return BooleanElement.TRUE;
            }
            throw new Exception("invalid element after \"!\"");
        } else if("-".equals(operator)) {
            if(rightResult.type() != Element.Type.INTEGER) {
                throw new Exception("invalid element after \"-\"");
            }
            IntegerElement rightIntegerElement = (IntegerElement) rightResult;
            return new IntegerElement(rightIntegerElement.getValue()*(-1));
        } else if("+".equals(operator)) {
            if(rightResult.type() != Element.Type.INTEGER) {
                throw new Exception("invalid element after \"+\"");
            }
            IntegerElement rightIntegerElement = (IntegerElement) rightResult;
            return new IntegerElement(rightIntegerElement.getValue());
        }
        throw new Exception(String.format("unsupported prefix \"%s\"", operator));
    }

    private Element handleInfixExpression(InfixExpression expression) throws Exception {
        Element leftResult = this.eval(expression.getLeft());
        String operator = expression.getOperator();
        Element rightResult = this.eval(expression.getRight());
        if(leftResult.type()==Element.Type.INTEGER && rightResult.type()==Element.Type.INTEGER) {
            IntegerElement leftIntegerElement = (IntegerElement) leftResult;
            IntegerElement rightIntegerElement = (IntegerElement) rightResult;
            if("+".equals(operator)) {
                return new IntegerElement(leftIntegerElement.getValue()+rightIntegerElement.getValue());
            } else if("-".equals(operator)) {
                return new IntegerElement(leftIntegerElement.getValue()-rightIntegerElement.getValue());
            } else if("*".equals(operator)) {
                return new IntegerElement(leftIntegerElement.getValue()*rightIntegerElement.getValue());
            } else if("/".equals(operator)) {
                return new IntegerElement(leftIntegerElement.getValue()/rightIntegerElement.getValue());
            } else if(">".equals(operator)) {
                return BooleanElement.of(leftIntegerElement.getValue()>rightIntegerElement.getValue());
            } else if("<".equals(operator)) {
                return BooleanElement.of(leftIntegerElement.getValue()<rightIntegerElement.getValue());
            } else if("==".equals(operator)) {
                return BooleanElement.of(leftIntegerElement.getValue()==rightIntegerElement.getValue());
            } else if("!=".equals(operator)) {
                return BooleanElement.of(leftIntegerElement.getValue()!=rightIntegerElement.getValue());
            }
            throw new Exception(String.format("unsupported operator \"%s\"", operator));
        } else if(leftResult.type()==Element.Type.BOOLEAN && rightResult.type()==Element.Type.BOOLEAN) {
            if("==".equals(operator)) {
                return BooleanElement.of(leftResult==rightResult);
            } else if("!=".equals(operator)) {
                return BooleanElement.of(leftResult!=rightResult);
            }
            throw new Exception(String.format("unsupported operator \"%s\"", operator));
        }
        throw new Exception(String.format("invalid expression:\n%s", expression.string()));
    }

//    private Element evalProgram(Program program) {
//        List<Statement> statementList = program.getStatementList();
//        if(statementList==null || statementList.size()==0) {
//            return null;
//        }
//    }
}