package com.leebuntu.server.communication.router;

@FunctionalInterface
public interface Middleware {
    boolean process(Context context) throws Exception;
}
