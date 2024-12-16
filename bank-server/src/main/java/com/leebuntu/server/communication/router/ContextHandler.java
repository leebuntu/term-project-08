package com.leebuntu.server.communication.router;

@FunctionalInterface
public interface ContextHandler {
    void handle(Context context) throws Exception;
}
