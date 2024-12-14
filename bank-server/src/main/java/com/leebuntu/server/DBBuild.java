package com.leebuntu.server;

import com.leebuntu.server.db.storage.Column;
import com.leebuntu.server.db.storage.DatabaseBuilder;
import com.leebuntu.server.db.storage.Table;
import com.leebuntu.server.db.storage.enums.ColumnType;

import java.io.IOException;

public class DBBuild {

    public static boolean buildUsersDB() throws IOException {
        DatabaseBuilder builder = new DatabaseBuilder();

        Table userTable = new Table("user");
        userTable.addColumn(new Column(ColumnType.INT, true, "id"));
        userTable.addColumn(new Column(ColumnType.INT, "customer_type"));
        userTable.addColumn(new Column(ColumnType.VARCHAR, 20, "customer_id"));
        userTable.addColumn(new Column(ColumnType.VARCHAR, 32, "password"));

        Table userInfoTable = new Table("user_info");
        userInfoTable.addColumn(new Column(ColumnType.INT, true, "id"));
        userInfoTable.addColumn(new Column(ColumnType.VARCHAR, 30, "name"));
        userInfoTable.addColumn(new Column(ColumnType.VARCHAR, 255, "address"));
        userInfoTable.addColumn(new Column(ColumnType.VARCHAR, 16, "phone"));
        userInfoTable.addColumn(new Column(ColumnType.INT, "credit_score"));

        return builder.setDatabaseName("customers").addTable(userTable).addTable(userInfoTable).create();
    }

    public static boolean buildAccountsDB() throws IOException {
        DatabaseBuilder builder = new DatabaseBuilder();

        Table accountTable = new Table("checking_account");
        accountTable.addColumn(new Column(ColumnType.INT, true, "id"));
        accountTable.addColumn(new Column(ColumnType.INT, "customer_id"));
        accountTable.addColumn(new Column(ColumnType.VARCHAR, 20, "account_number"));
        accountTable.addColumn(new Column(ColumnType.LONG, "total_balance"));
        accountTable.addColumn(new Column(ColumnType.LONG, "available_balance"));
        accountTable.addColumn(new Column(ColumnType.LONG, "open_date"));
        accountTable.addColumn(new Column(ColumnType.VARCHAR, 20, "linked_savings_account_number"));

        Table savingsAccountTable = new Table("savings_account");
        savingsAccountTable.addColumn(new Column(ColumnType.INT, true, "id"));
        savingsAccountTable.addColumn(new Column(ColumnType.INT, "customer_id"));
        savingsAccountTable.addColumn(new Column(ColumnType.VARCHAR, 20, "account_number"));
        savingsAccountTable.addColumn(new Column(ColumnType.LONG, "total_balance"));
        savingsAccountTable.addColumn(new Column(ColumnType.LONG, "available_balance"));
        savingsAccountTable.addColumn(new Column(ColumnType.LONG, "open_date"));
        savingsAccountTable.addColumn(new Column(ColumnType.DOUBLE, "interest_rate"));
        savingsAccountTable.addColumn(new Column(ColumnType.LONG, "max_transfer_amount_to_checking"));

        return builder.setDatabaseName("accounts").addTable(accountTable).addTable(savingsAccountTable).create();
    }

    public static boolean buildTransactionsDB() throws IOException {
        DatabaseBuilder builder = new DatabaseBuilder();

        Table checkingTransactionTable = new Table("checking_transaction");
        checkingTransactionTable.addColumn(new Column(ColumnType.INT, true, "id"));
        checkingTransactionTable.addColumn(new Column(ColumnType.INT, "sender_id"));
        checkingTransactionTable.addColumn(new Column(ColumnType.INT, "receiver_id"));
        checkingTransactionTable.addColumn(new Column(ColumnType.VARCHAR, 20, "sender_account_number"));
        checkingTransactionTable.addColumn(new Column(ColumnType.VARCHAR, 20, "receiver_account_number"));
        checkingTransactionTable.addColumn(new Column(ColumnType.LONG, "amount"));
        checkingTransactionTable.addColumn(new Column(ColumnType.LONG, "date"));

        Table savingsTransactionTable = new Table("savings_transaction");
        savingsTransactionTable.addColumn(new Column(ColumnType.INT, true, "id"));
        savingsTransactionTable.addColumn(new Column(ColumnType.INT, "sender_id"));
        savingsTransactionTable.addColumn(new Column(ColumnType.INT, "receiver_id"));
        savingsTransactionTable.addColumn(new Column(ColumnType.VARCHAR, 20, "sender_account_number"));
        savingsTransactionTable.addColumn(new Column(ColumnType.VARCHAR, 20, "receiver_account_number"));
        savingsTransactionTable.addColumn(new Column(ColumnType.LONG, "amount"));
        savingsTransactionTable.addColumn(new Column(ColumnType.LONG, "date"));

        return builder.setDatabaseName("transactions").addTable(checkingTransactionTable)
                .addTable(savingsTransactionTable)
                .create();
    }

    public static boolean buildLoansDB() throws IOException {
        DatabaseBuilder builder = new DatabaseBuilder();

        Table loanTable = new Table("loan");
        loanTable.addColumn(new Column(ColumnType.INT, true, "id"));
        loanTable.addColumn(new Column(ColumnType.INT, "customer_id"));
        loanTable.addColumn(new Column(ColumnType.VARCHAR, 20, "account_number"));
        loanTable.addColumn(new Column(ColumnType.LONG, "amount"));
        loanTable.addColumn(new Column(ColumnType.LONG, "date"));
        loanTable.addColumn(new Column(ColumnType.LONG, "due_date"));
        loanTable.addColumn(new Column(ColumnType.LONG, "interest_rate"));

        return builder.setDatabaseName("loans").addTable(loanTable).create();
    }
}
