package org.edward.pandora.monkey.model;

import org.edward.pandora.monkey.model.evaluator.BooleanElement;
import org.edward.pandora.monkey.model.evaluator.IntegerElement;
import org.edward.pandora.monkey.model.expression.BooleanExpression;
import org.edward.pandora.monkey.model.expression.IntegerExpression;
import org.edward.pandora.monkey.model.expression.PrefixExpression;
import org.edward.pandora.monkey.model.statement.ExpressionStatement;

import java.util.List;

public class Evaluator {
    public Element eval(Node node) throws Exception {
        if(node instanceof IntegerExpression) {
            IntegerExpression integerExpression = (IntegerExpression) node;
            return new IntegerElement(integerExpression.getValue());
        } else if(node instanceof BooleanExpression) {
            BooleanExpression booleanExpression = (BooleanExpression) node;
            return BooleanElement.of(booleanExpression.isValue());
        } else if(node instanceof ExpressionStatement) {
            ExpressionStatement expressionStatement = (ExpressionStatement) node;
            return this.eval(expressionStatement.getExpression());
        } else if(node instanceof PrefixExpression) {
            PrefixExpression prefixExpression = (PrefixExpression) node;
            String operator = prefixExpression.getOperator();
            Element rightResult = this.eval(prefixExpression.getRight());
            if("!".equals(operator)) {
                if(rightResult == BooleanElement.TRUE) {
                    return BooleanElement.FALSE;
                } else if(rightResult == BooleanElement.FALSE) {
                    return BooleanElement.TRUE;
                } else if(rightResult instanceof IntegerElement) {
                    IntegerElement rightIntegerElement = (IntegerElement) rightResult;
                    if(rightIntegerElement.getValue() > 0) {
                        return BooleanElement.FALSE;
                    }
                    return BooleanElement.TRUE;
                }
                throw new Exception("invalid element after \"!\"");
            } else if("-".equals(operator)) {
                if(rightResult instanceof IntegerElement) {
                    IntegerElement rightIntegerElement = (IntegerElement) rightResult;
                    return new IntegerElement(rightIntegerElement.getValue()*(-1));
                }
                throw new Exception("invalid element after \"-\"");
            }
            return null;
        } else if(node instanceof Program) {
            Program program = (Program) node;
            return this.evalStatements(program.getStatementList());
        }
        return null;
    }

    private Element evalStatements(List<Statement> statementList) throws Exception {
        if(statementList==null || statementList.size()==0) {
            return null;
        }
        Element result = null;
        for(Statement statement : statementList) {
            result = this.eval(statement);
        }
        return result;
    }

//    private Element evalProgram(Program program) {
//        List<Statement> statementList = program.getStatementList();
//        if(statementList==null || statementList.size()==0) {
//            return null;
//        }
//    }
}