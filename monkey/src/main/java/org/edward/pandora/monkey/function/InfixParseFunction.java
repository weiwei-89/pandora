package org.edward.pandora.monkey.function;

import org.edward.pandora.monkey.model.Expression;

@FunctionalInterface
public interface InfixParseFunction {
    Expression apply(Expression expression) throws Exception;
}