package com.leebuntu.common.communication.router;

@FunctionalInterface
public interface ContextHandler {
    void handle(Context context) throws Exception;
}
