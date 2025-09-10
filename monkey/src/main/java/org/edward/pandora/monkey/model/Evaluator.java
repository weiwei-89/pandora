package org.edward.pandora.monkey.model;

import org.edward.pandora.monkey.exception.EvaluateException;
import org.edward.pandora.monkey.model.evaluator.*;
import org.edward.pandora.monkey.model.expression.*;
import org.edward.pandora.monkey.model.statement.BlockStatement;
import org.edward.pandora.monkey.model.statement.ExpressionStatement;
import org.edward.pandora.monkey.model.statement.ReturnStatement;

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
        this.register(IfExpression.class, this::handleIfExpression);
        this.register(BlockStatement.class, this::handleBlockStatement);
        this.register(ReturnStatement.class, this::handleReturnStatement);
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
            throw new EvaluateException(String.format("unsupported expression:\n%s", node.string()));
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
            return NullElement.INSTANCE;
        }
        Element result = null;
        for(Statement statement : statementList) {
            result = this.eval(statement);
            if(result!=null && result.type()==Element.Type.RETURN) {
                return result;
            }
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
                    return BooleanElement.TRUE;
                }
                return BooleanElement.FALSE;
            }
            throw new EvaluateException("invalid element after \"!\"");
        } else if("-".equals(operator)) {
            if(rightResult.type() != Element.Type.INTEGER) {
                throw new EvaluateException("invalid element after \"-\"");
            }
            IntegerElement rightIntegerElement = (IntegerElement) rightResult;
            return new IntegerElement(rightIntegerElement.getValue()*(-1));
        } else if("+".equals(operator)) {
            if(rightResult.type() != Element.Type.INTEGER) {
                throw new EvaluateException("invalid element after \"+\"");
            }
            IntegerElement rightIntegerElement = (IntegerElement) rightResult;
            return new IntegerElement(rightIntegerElement.getValue());
        }
        throw new EvaluateException(String.format("unsupported prefix \"%s\"", operator));
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
            throw new EvaluateException(String.format("unsupported operator \"%s\"", operator));
        } else if(leftResult.type()==Element.Type.BOOLEAN && rightResult.type()==Element.Type.BOOLEAN) {
            if("==".equals(operator)) {
                return BooleanElement.of(leftResult==rightResult);
            } else if("!=".equals(operator)) {
                return BooleanElement.of(leftResult!=rightResult);
            }
            throw new EvaluateException(String.format("unsupported operator \"%s\"", operator));
        }
        throw new EvaluateException(String.format("invalid expression:\n%s", expression.string()));
    }

    private Element handleIfExpression(IfExpression expression) throws Exception {
        Element conditionResult = this.eval(expression.getCondition());
        boolean _conditionResult = false;
        if(conditionResult.type() == Element.Type.BOOLEAN) {
            _conditionResult = ((BooleanElement) conditionResult).getValue();
        } else if(conditionResult.type() == Element.Type.INTEGER) {
            IntegerElement conditionIntegerResult = (IntegerElement) conditionResult;
            _conditionResult = conditionIntegerResult.getValue()>0?true:false;
        } else {
            throw new EvaluateException(String.format("invalid condition:\n%s", expression.getCondition().string()));
        }
        if(_conditionResult) {
            return this.eval(expression.getConsequence());
        } else {
            if(expression.getAlternative() == null) {
                return NullElement.INSTANCE;
            }
            return this.eval(expression.getAlternative());
        }
    }

    private Element handleBlockStatement(BlockStatement statement) throws Exception {
        List<Statement> statementList = statement.getStatementList();
        if(statementList==null || statementList.size()==0) {
            return NullElement.INSTANCE;
        }
        Element result = null;
        for(Statement _statement : statementList) {
            result = this.eval(_statement);
            if(result!=null && result.type()==Element.Type.RETURN) {
                return result;
            }
        }
        return result;
    }

    private Element handleReturnStatement(ReturnStatement statement) throws Exception {
        Element returnResult = this.eval(statement.getValue());
        return new ReturnElement(returnResult);
    }

//    private Element evalProgram(Program program) {
//        List<Statement> statementList = program.getStatementList();
//        if(statementList==null || statementList.size()==0) {
//            return null;
//        }
//    }
}