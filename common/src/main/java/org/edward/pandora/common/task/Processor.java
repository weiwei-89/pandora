package org.edward.pandora.common.task;

public interface Processor {
    void init() throws Exception;

    void process() throws Exception;
}