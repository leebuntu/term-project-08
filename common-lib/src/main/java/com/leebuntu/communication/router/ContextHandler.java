package com.leebuntu.communication.router;

@FunctionalInterface
public interface ContextHandler {
    void handle(Context context) throws Exception;
}
