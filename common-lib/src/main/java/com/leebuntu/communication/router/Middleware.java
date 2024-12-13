package com.leebuntu.communication.router;

@FunctionalInterface
public interface Middleware {
    boolean process(Context context);
}
