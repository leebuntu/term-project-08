package com.leebuntu.server.handler.user.banking;

import com.leebuntu.common.communication.router.ContextHandler;
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;

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
