package com.leebuntu.server.provider;

import java.util.ArrayList;
import java.util.List;

import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.account.AccountType;
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;
import com.leebuntu.server.db.query.QueryResult;
import com.leebuntu.server.db.query.enums.QueryStatus;

public class AccountProvider {
    private static final Database accountDB = DatabaseManager.getDB("accounts");

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

    public static boolean isAcountOwner(String accountNumber, int userId) {
        String typeName = getAccountType(accountNumber).name().toLowerCase();
        String query = "SELECT customer_id FROM " + typeName + "_account WHERE account_number = ?";
        QueryResult result = accountDB.execute(query, accountNumber);
        return result.getRowCount() > 0 && (int) result.getCurrentRow().get(0) == userId;
    }

    public static AccountType getAccountType(String accountNumber) {
        String query = "SELECT id FROM checking_account WHERE account_number = ?";
        QueryResult result = accountDB.execute(query, accountNumber);
        if (result.getQueryStatus() == QueryStatus.SUCCESS) {
            return AccountType.CHECKING;
        }

        query = "SELECT id FROM savings_account WHERE account_number = ?";
        QueryResult result2 = accountDB.execute(query, accountNumber);
        if (result2.getQueryStatus() == QueryStatus.SUCCESS) {
            return AccountType.SAVINGS;
        }

        return null;
    }

    private static QueryResult createCheckingAccount(Account account) {
        String query = "INSERT INTO checking_account customer_id, account_number, total_balance, available_balance, open_date, linked_savings_account_number";
        return accountDB.execute(query, account.getCustomerId(), account.getAccountNumber(),
                account.getTotalBalance(), account.getAvailableBalance(), account.getOpenDate(),
                account.getLinkedSavingsAccountNumber());
    }

    private static QueryResult createSavingsAccount(Account account) {
        String query = "INSERT INTO savings_account customer_id, account_number, total_balance, available_balance, open_date, interest_rate, max_transfer_amount_to_checking";
        return accountDB.execute(query, account.getCustomerId(), account.getAccountNumber(),
                account.getTotalBalance(), account.getAvailableBalance(), account.getOpenDate(),
                account.getInterestRate(), account.getMaxTransferAmountToChecking());
    }

    public static boolean createAccount(Account account) {
        switch (account.getAccountType()) {
            case CHECKING:
                return createCheckingAccount(account).getQueryStatus() == QueryStatus.SUCCESS;
            case SAVINGS:
                return createSavingsAccount(account).getQueryStatus() == QueryStatus.SUCCESS;
            default:
                return false;
        }
    }

    public static Account getAccount(String accountNumber) {
        switch (getAccountType(accountNumber)) {
            case CHECKING:
                String query = "SELECT * FROM checking_account WHERE account_number = ?";
                QueryResult result = accountDB.execute(query, accountNumber);
                return result.getRowCount() > 0 ? new Account((int) result.getCurrentRow().get(0),
                        (int) result.getCurrentRow().get(1), (String) result.getCurrentRow().get(2),
                        (Long) result.getCurrentRow().get(3), (Long) result.getCurrentRow().get(4),
                        (Long) result.getCurrentRow().get(5), AccountType.CHECKING,
                        (String) result.getCurrentRow().get(6), 0.0,
                        0L) : null;
            case SAVINGS:
                query = "SELECT * FROM savings_account WHERE account_number = ?";
                result = accountDB.execute(query, accountNumber);
                return result.getRowCount() > 0 ? new Account((int) result.getCurrentRow().get(0),
                        (int) result.getCurrentRow().get(1), (String) result.getCurrentRow().get(2),
                        (Long) result.getCurrentRow().get(3), (Long) result.getCurrentRow().get(4),
                        (Long) result.getCurrentRow().get(5), AccountType.SAVINGS,
                        "", (double) result.getCurrentRow().get(6),
                        (Long) result.getCurrentRow().get(7)) : null;
            default:
                return null;
        }
    }

    public static List<Account> getAccounts(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM checking_account WHERE customer_id = ?";
        QueryResult result = accountDB.execute(query, customerId);
        if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
            return new ArrayList<>();
        }

        while (result.next()) {
            List<Object> row = result.getCurrentRow();
            accounts.add(new Account((int) row.get(0), (int) row.get(1), (String) row.get(2), (Long) row.get(3),
                    (Long) row.get(4), (Long) row.get(5), AccountType.CHECKING, (String) row.get(6), 0.0,
                    0L));
        }

        query = "SELECT * FROM savings_account WHERE customer_id = ?";
        result = accountDB.execute(query, customerId);
        if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
            return new ArrayList<>();
        }

        while (result.next()) {
            List<Object> row = result.getCurrentRow();
            accounts.add(new Account((int) row.get(0), (int) row.get(1), (String) row.get(2), (Long) row.get(3),
                    (Long) row.get(4), (Long) row.get(5), AccountType.SAVINGS, "", (double) row.get(6),
                    (Long) row.get(7)));
        }

        return accounts;
    }

    public static List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM checking_account";
        QueryResult result = accountDB.execute(query);
        if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
            return new ArrayList<>();
        }

        while (result.next()) {
            List<Object> row = result.getCurrentRow();
            accounts.add(new Account((int) row.get(0), (int) row.get(1), (String) row.get(2), (Long) row.get(3),
                    (Long) row.get(4), (Long) row.get(5), AccountType.CHECKING, (String) row.get(6), 0.0, 0L));
        }

        query = "SELECT * FROM savings_account";
        result = accountDB.execute(query);
        if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
            return new ArrayList<>();
        }

        while (result.next()) {
            List<Object> row = result.getCurrentRow();
            accounts.add(new Account((int) row.get(0), (int) row.get(1), (String) row.get(2), (Long) row.get(3),
                    (Long) row.get(4), (Long) row.get(5), AccountType.SAVINGS, "", (double) row.get(6),
                    (Long) row.get(7)));
        }

        return accounts;
    }

    public static boolean updateAccountBalance(String accountNumber, Long amount) {
        String typeName = getAccountType(accountNumber).name().toLowerCase();

        String query = "UPDATE FROM " + typeName + "_account total_balance = ? WHERE account_number = ?";
        QueryResult result = accountDB.execute(query, amount, accountNumber);
        return result.getQueryStatus() == QueryStatus.SUCCESS;
    }

    private static boolean updateCheckingAccount(Account account) {
        String query = "UPDATE FROM checking_account total_balance = ?, available_balance = ?, linked_savings_account_number = ? WHERE id = ?";
        QueryResult result = accountDB.execute(query, account.getTotalBalance(), account.getAvailableBalance(),
                account.getLinkedSavingsAccountNumber(), account.getId());
        return result.getQueryStatus() == QueryStatus.SUCCESS;
    }

    private static boolean updateSavingsAccount(Account account) {
        String query = "UPDATE FROM savings_account total_balance = ?, available_balance = ?, interest_rate = ?, max_transfer_amount_to_checking = ? WHERE id = ?";
        QueryResult result = accountDB.execute(query, account.getTotalBalance(), account.getAvailableBalance(),
                account.getInterestRate(), account.getMaxTransferAmountToChecking(), account.getId());
        return result.getQueryStatus() == QueryStatus.SUCCESS;
    }

    public static boolean updateAccount(Account account) {
        switch (account.getAccountType()) {
            case CHECKING:
                return updateCheckingAccount(account);
            case SAVINGS:
                return updateSavingsAccount(account);
            default:
                return false;
        }
    }

    public static boolean deleteAccount(String accountNumber) {
        String typeName = getAccountType(accountNumber).name().toLowerCase();

        String query = "DELETE FROM " + typeName + "_account WHERE account_number = ?";
        QueryResult result = accountDB.execute(query, accountNumber);
        return result.getQueryStatus() == QueryStatus.SUCCESS;
    }
}
