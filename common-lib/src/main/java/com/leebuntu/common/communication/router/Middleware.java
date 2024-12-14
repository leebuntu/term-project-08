package com.leebuntu.common.communication.router;

@FunctionalInterface
public interface Middleware {
    boolean process(Context context) throws Exception;
}
