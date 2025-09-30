package org.edward.pandora.monkey.model;

import org.edward.pandora.monkey.exception.EvaluateException;
import org.edward.pandora.monkey.model.evaluator.*;
import org.edward.pandora.monkey.model.expression.*;
import org.edward.pandora.monkey.model.statement.BlockStatement;
import org.edward.pandora.monkey.model.statement.ExpressionStatement;
import org.edward.pandora.monkey.model.statement.LetStatement;
import org.edward.pandora.monkey.model.statement.ReturnStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
    private static final Logger logger = LoggerFactory.getLogger(Evaluator.class);
    private final Map<Class<? extends Node>, EvalFunction<Node, Environment, Element>> handlers = new HashMap<>();

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
        this.register(LetStatement.class, this::handleLetStatement);
        this.register(IdentifierExpression.class, this::handleIdentifierExpression);
        this.register(FunctionLiteralExpression.class, this::handleFunctionLiteralExpression);
        this.register(CallExpression.class, this::handleCallExpression);
    }

    private <N extends Node> void register(Class<N> nodeType, EvalFunction<N, Environment, Element> handler) {
        this.handlers.put(
                nodeType,
                (node, env) -> {
                    logger.info("invoke function for {}", nodeType.getName());
                    return handler.apply(nodeType.cast(node), env);
                }
        );
    }

    public Element eval(Node node, Environment env) throws Exception {
        EvalFunction<Node, Environment, Element> handler = this.handlers.get(node.getClass());
        if(handler == null) {
            throw new EvaluateException(String.format("unsupported expression:\n%s", node.string()));
        }
        return handler.apply(node, env);
    }

    private Element handleIntegerExpression(IntegerExpression expression, Environment env) throws Exception {
        return new IntegerElement(expression.getValue());
    }

    private Element handleBooleanExpression(BooleanExpression expression, Environment env) throws Exception {
        return BooleanElement.of(expression.isValue());
    }

    private Element handleProgram(Program program, Environment env) throws Exception {
        return this.handleBlockStatement(program, env);
    }

    private Element handleExpressionStatement(ExpressionStatement statement, Environment env) throws Exception {
        return this.eval(statement.getExpression(), env);
    }

    private Element handlePrefixExpression(PrefixExpression expression, Environment env) throws Exception {
        String operator = expression.getOperator();
        Element rightResult = this.eval(expression.getRight(), env);
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

    private Element handleInfixExpression(InfixExpression expression, Environment env) throws Exception {
        Element leftResult = this.eval(expression.getLeft(), env);
        String operator = expression.getOperator();
        Element rightResult = this.eval(expression.getRight(), env);
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

    private Element handleIfExpression(IfExpression expression, Environment env) throws Exception {
        Element conditionResult = this.eval(expression.getCondition(), env);
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
            return this.eval(expression.getConsequence(), env);
        } else {
            if(expression.getAlternative() == null) {
                return VoidElement.INSTANCE;
            }
            return this.eval(expression.getAlternative(), env);
        }
    }

    private Element handleBlockStatement(BlockStatement statement, Environment env) throws Exception {
        List<Statement> statementList = statement.getStatementList();
        if(statementList==null || statementList.size()==0) {
            return VoidElement.INSTANCE;
        }
        Element result = null;
        for(Statement _statement : statementList) {
            result = this.eval(_statement, env);
            if(result == null) {
                continue;
            }
            if(result.type() == Element.Type.VOID) {
                continue;
            }
            if(result.type() == Element.Type.RETURN) {
                return result;
            }
        }
        return result;
    }

    private Element handleReturnStatement(ReturnStatement statement, Environment env) throws Exception {
        Element returnResult = this.eval(statement.getValue(), env);
        return new ReturnElement(returnResult);
    }

    private Element handleLetStatement(LetStatement statement, Environment env) throws Exception {
        Element valueResult = this.eval(statement.getValue(), env);
        env.set(statement.getName().getValue(), valueResult);
        return VoidElement.INSTANCE;
    }

    private Element handleIdentifierExpression(IdentifierExpression expression, Environment env) throws Exception {
        Element value = env.get(expression.getValue());
        if(value == null) {
            throw new EvaluateException(String.format("identifier \"%s\" not initialized", expression.getValue()));
        }
        return value;
    }

    private Element handleFunctionLiteralExpression(FunctionLiteralExpression expression, Environment env) throws Exception {
        return new FunctionElement(expression.getParameters(), expression.getBody(), env);
    }

    private Element handleCallExpression(CallExpression expression, Environment env) throws Exception {
        Element functionResult = this.eval(expression.getFunction(), env);
        if(functionResult==null || functionResult.type()!=Element.Type.FUNCTION) {
            throw new EvaluateException(String.format("\"%s\" is not a function", expression.string()));
        }
        FunctionElement functionElement = (FunctionElement) functionResult;
        List<Element> argumentResultList = null;
        List<Expression> arguments = expression.getArguments();
        if(arguments!=null && arguments.size()>0) {
            argumentResultList = new ArrayList<>(arguments.size());
            for(Expression argument : arguments) {
                argumentResultList.add(this.eval(argument, env));
            }
        }
        Environment exEnv = new Environment(functionElement.getEnv());
        List<IdentifierExpression> parameters = functionElement.getParameters();
        if(parameters!=null && parameters.size()>0) {
            for(int p=0; p<parameters.size(); p++) {
                IdentifierExpression parameter = parameters.get(p);
                exEnv.set(parameter.getValue(), argumentResultList.get(p));
            }
        }
        Element callResult = this.eval(functionElement.getBody(), exEnv);
        if(callResult == null) {
            return NullElement.INSTANCE;
        }
        if(callResult.type() == Element.Type.RETURN) {
            ReturnElement returnResult = (ReturnElement) callResult;
            return returnResult.getValue();
        }
        return callResult;
    }
}