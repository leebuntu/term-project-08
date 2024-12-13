package com.leebuntu.handler.util;

import com.leebuntu.banking.account.AccountType;
import com.leebuntu.banking.customer.CustomerType;
import com.leebuntu.db.core.Database;
import com.leebuntu.db.core.DatabaseManager;
import com.leebuntu.db.query.QueryResult;
import com.leebuntu.db.query.enums.QueryStatus;

public class Utils {
    private static final Database customerDB = DatabaseManager.getDB("customers");
    private static final Database accountDB = DatabaseManager.getDB("accounts");

    public static boolean isExistUser(int customerId) {
        String query = "SELECT id FROM user WHERE id = ?";
        QueryResult result = customerDB.execute(query, customerId);
        return result.getRowCount() > 0;
    }

    public static boolean isAccountNumberExist(String accountNumber) {
        String query = "SELECT id FROM checking_account WHERE account_number = ?";
        QueryResult result = accountDB.execute(query, accountNumber);

        query = "SELECT id FROM savings_account WHERE account_number = ?";
        QueryResult result2 = accountDB.execute(query, accountNumber);
        return result.getRowCount() > 0 || result2.getRowCount() > 0;
    }

    public static boolean isSavingsAccountIdExist(int savingsAccountId) {
        String query = "SELECT id FROM savings_account WHERE id = ?";
        QueryResult result = accountDB.execute(query, savingsAccountId);
        return result.getRowCount() > 0;
    }

    public static boolean isAdmin(int userId) {
        String query = "SELECT customer_type FROM user WHERE id = ?";
        QueryResult result = customerDB.execute(query, userId);
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return false;
        }

        return (int) result.getCurrentRow().get(0) == CustomerType.ADMIN.ordinal();
    }

    public static AccountType getAccountType(String accountNumber) {
        String query = "SELECT account_type FROM account WHERE account_number = ?";
        QueryResult result = accountDB.execute(query, accountNumber);
        if (result.getQueryStatus() == QueryStatus.SUCCESS) {
            return AccountType.valueOf((String) result.getCurrentRow().get(0));
        }

        query = "SELECT account_type FROM savings_account WHERE account_number = ?";
        QueryResult result2 = accountDB.execute(query, accountNumber);
        if (result2.getQueryStatus() == QueryStatus.SUCCESS) {
            return AccountType.valueOf((String) result2.getCurrentRow().get(0));
        }

        return null;
    }
}
