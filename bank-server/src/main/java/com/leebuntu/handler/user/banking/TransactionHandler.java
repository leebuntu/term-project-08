package com.leebuntu.handler.user.banking;


import com.leebuntu.banking.Transaction;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.dto.request.banking.GetTransactions;
import com.leebuntu.communication.dto.response.banking.Transactions;
import com.leebuntu.communication.router.ContextHandler;
import com.leebuntu.db.core.Database;
import com.leebuntu.db.core.DatabaseManager;
import com.leebuntu.db.query.QueryResult;
import com.leebuntu.db.query.enums.QueryStatus;
import com.leebuntu.handler.util.Utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransactionHandler {
    private static final Database accountDB = DatabaseManager.getDB("accounts");
    private static final Database transactionDB = DatabaseManager.getDB("transactions");

    private static List<Transaction> getTransactionsByString(String typeName, int accountId) {
        List<Transaction> transactions = new ArrayList<>();

        String query = "SELECT * FROM " + typeName + "_transaction WHERE sender_account_id = ?";
        QueryResult result = transactionDB.execute(query, accountId);
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return new ArrayList<>();
        }

        while (result.next()) {
            transactions.add(new Transaction((int) result.getCurrentRow().get(1), (int) result.getCurrentRow().get(2),
                    (int) result.getCurrentRow().get(3), (int) result.getCurrentRow().get(4),
                    (Long) result.getCurrentRow().get(5), Instant.ofEpochMilli((long) result.getCurrentRow().get(6))));
        }

        query = "SELECT * FROM " + typeName + "_transaction WHERE receiver_account_id = ?";
        result = transactionDB.execute(query, accountId);
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return new ArrayList<>();
        }

        while (result.next()) {
            transactions.add(new Transaction((int) result.getCurrentRow().get(1), (int) result.getCurrentRow().get(2),
                    (int) result.getCurrentRow().get(3), (int) result.getCurrentRow().get(4),
                    (Long) result.getCurrentRow().get(5), Instant.ofEpochMilli((long) result.getCurrentRow().get(6))));
        }

        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        return transactions;
    }

    public static ContextHandler getTransactions() {
        return context -> {
            int userId = (int) context.getField("userId");

            GetTransactions request = new GetTransactions();
            if (context.bind(request)) {
                List<Transaction> transactions = new ArrayList<>();
                String typeName = "";

                switch (Utils.getAccountType(request.getAccountNumber())) {
                    case CHECKING:
                        typeName = "checking";
                        break;
                    case SAVINGS:
                        typeName = "savings";
                        break;
                }

                String query = "SELECT id, customer_id FROM " + typeName + "_account WHERE account_number = ?";
                QueryResult result = accountDB.execute(query, request.getAccountNumber());
                if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.FAILED, "Failed to get " + typeName + " account"));
                    return;
                }

                if ((int) result.getCurrentRow().get(1) != userId) {
                    context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                    return;
                }

                int accountId = (int) result.getCurrentRow().get(0);

                transactions.addAll(getTransactionsByString(typeName, accountId));

                context.reply(new Transactions(Status.SUCCESS, "Transactions fetched successfully",
                        transactions));

            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }
}
