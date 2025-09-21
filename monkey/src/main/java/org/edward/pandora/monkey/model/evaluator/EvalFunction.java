package org.edward.pandora.monkey.model.evaluator;

@FunctionalInterface
public interface EvalFunction<N, E, R> {
    R apply(N node, E env) throws Exception;
}