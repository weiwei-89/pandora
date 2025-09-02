package org.edward.pandora.monkey.model.evaluator;

@FunctionalInterface
public interface EvalFunction<T, R> {
    R apply(T t) throws Exception;
}