package com.leebuntu.server.provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.leebuntu.common.banking.Transaction;
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;
import com.leebuntu.server.db.query.QueryResult;
import com.leebuntu.server.db.query.enums.QueryStatus;

public class TransactionProvider {
    private static final Database transactionDB = DatabaseManager.getDB("transactions");

    public static boolean addTransaction(Transaction transaction) {
        String typeName = AccountProvider
                .getAccountType(transaction.getSenderAccountNumber() == "ATM" ? transaction.getReceiverAccountNumber()
                        : transaction.getSenderAccountNumber())
                .name().toLowerCase();
        String query = "INSERT INTO " + typeName
                + "_transaction sender_id, receiver_id, sender_account_number, receiver_account_number, amount, date";
        QueryResult result = transactionDB.execute(query, transaction.getSenderId(), transaction.getReceiverId(),
                transaction.getSenderAccountNumber(), transaction.getReceiverAccountNumber(), transaction.getAmount(),
                transaction.getDate());

        return result.getQueryStatus() == QueryStatus.SUCCESS;
    }

    public static List<Transaction> getTransactions(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();

        String typeName = AccountProvider.getAccountType(accountNumber).name().toLowerCase();
        String query = "SELECT * FROM " + typeName
                + "_transaction WHERE sender_account_number = ?";

        QueryResult result = transactionDB.execute(query, accountNumber);
        if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
            return new ArrayList<>();
        }

        while (result.next()) {
            transactions.add(new Transaction((int) result.getCurrentRow().get(1), (int) result.getCurrentRow().get(2),
                    (String) result.getCurrentRow().get(3), (String) result.getCurrentRow().get(4),
                    (Long) result.getCurrentRow().get(5), (Long) result.getCurrentRow().get(6)));
        }

        query = "SELECT * FROM " + typeName + "_transaction WHERE receiver_account_number = ?";
        result = transactionDB.execute(query, accountNumber);
        if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
            return new ArrayList<>();
        }

        while (result.next()) {
            transactions.add(new Transaction((int) result.getCurrentRow().get(1), (int) result.getCurrentRow().get(2),
                    (String) result.getCurrentRow().get(3), (String) result.getCurrentRow().get(4),
                    (Long) result.getCurrentRow().get(5), (Long) result.getCurrentRow().get(6)));
        }

        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        return transactions;
    }
}
