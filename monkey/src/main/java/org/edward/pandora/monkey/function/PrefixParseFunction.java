package org.edward.pandora.monkey.function;

import org.edward.pandora.monkey.model.Expression;

@FunctionalInterface
public interface PrefixParseFunction {
    Expression apply() throws Exception;
}