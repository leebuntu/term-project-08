package com.leebuntu.server.handler.user.banking;

import com.leebuntu.server.communication.router.ContextHandler;

public class LoanHandler {

    public static ContextHandler getCreditScore() {
        return context -> {
            int userId = (int) context.getField("userId");

        };
    }

    public static ContextHandler takeOutLoan() {
        return context -> {
            int userId = (int) context.getField("userId");

        };
    }

}
