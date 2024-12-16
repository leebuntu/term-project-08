package com.leebuntu.server.handler.user.banking;

import com.leebuntu.common.banking.Transaction;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.banking.dto.request.banking.GetTransactions;
import com.leebuntu.common.banking.dto.response.banking.Transactions;
import com.leebuntu.server.communication.router.ContextHandler;
import com.leebuntu.server.provider.AccountProvider;
import com.leebuntu.server.provider.TransactionProvider;

import java.util.List;

public class TransactionHandler {
    public static ContextHandler getTransactions() {
        return context -> {
            int userId = (int) context.getField("userId");

            GetTransactions request = new GetTransactions();
            if (context.bind(request)) {
                if (!AccountProvider.isAccountOwner(request.getAccountNumber(), userId)) {
                    context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                    return;
                }

                List<Transaction> transactions = TransactionProvider.getTransactions(request.getAccountNumber());
                if (transactions.isEmpty()) {
                    context.reply(new Response(Status.NOT_FOUND, "No transactions found"));
                } else {
                    context.reply(new Transactions(Status.SUCCESS, "Transactions fetched successfully", transactions));
                }

            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }
}
