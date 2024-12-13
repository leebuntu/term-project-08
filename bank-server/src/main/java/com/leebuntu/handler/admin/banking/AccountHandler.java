package com.leebuntu.handler.admin.banking;

import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.account.AccountType;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.dto.request.banking.CreateAccount;
import com.leebuntu.communication.dto.request.banking.RemoveAccount;
import com.leebuntu.communication.dto.request.banking.ViewAccount;
import com.leebuntu.communication.dto.response.banking.Accounts;
import com.leebuntu.communication.router.ContextHandler;
import com.leebuntu.db.core.Database;
import com.leebuntu.db.core.DatabaseManager;
import com.leebuntu.db.query.QueryResult;
import com.leebuntu.db.query.enums.QueryStatus;
import com.leebuntu.handler.util.Utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class AccountHandler {
    private static final Database db = DatabaseManager.getDB("accounts");

    private static QueryResult createCheckingAccount(Account account) {
        if (Utils.isAccountNumberExist(account.getAccountNumber())) {
            return new QueryResult(QueryStatus.DUPLICATED, "Account number already exists");
        }

        if (account.getLinkedSavingsAccountNumber() > -1
                && !Utils.isSavingsAccountIdExist(account.getLinkedSavingsAccountNumber())) {
            System.out.println("YO");
            return new QueryResult(QueryStatus.FAILED, "Savings account number does not exist");
        }

        if (!Utils.isExistUser(account.getCustomerId())) {
            System.out.println("YOYO");
            return new QueryResult(QueryStatus.FAILED, "Customer id does not exist");
        }

        String query = "INSERT INTO checking_account customer_id, account_number, total_balance, available_balance, open_date, linked_savings_id";

        return db.execute(query, account.getCustomerId(), account.getAccountNumber(),
                account.getTotalBalance(), account.getAvailableBalance(), account.getOpenDate(),
                account.getLinkedSavingsAccountNumber());
    }

    private static QueryResult createSavingsAccount(Account account) {
        if (Utils.isAccountNumberExist(account.getAccountNumber())) {
            return new QueryResult(QueryStatus.DUPLICATED, "Account number already exists");
        }

        if (!Utils.isExistUser(account.getCustomerId())) {
            return new QueryResult(QueryStatus.FAILED, "Customer id does not exist");
        }

        String query = "INSERT INTO savings_account customer_id, account_number, total_balance, available_balance, open_date, interest_rate, max_transfer_amount_to_checking";

        return db.execute(query, account.getCustomerId(), account.getAccountNumber(), account.getTotalBalance(),
                account.getAvailableBalance(), account.getOpenDate(), account.getInterestRate(),
                account.getMaxTransferAmountToChecking());
    }

    public static ContextHandler createAccount() {
        return (context) -> {
            CreateAccount request = new CreateAccount();

            if (context.bind(request)) {
                QueryResult result = null;

                try {
                    if (request.getAccountType() == AccountType.CHECKING) {
                        Account account = new Account(request.getCustomerId(), request.getAccountNumber(),
                                request.getTotalBalance(), request.getAvailableBalance(),
                                Instant.now().toEpochMilli(), request.getAccountType(),
                                request.getLinkedSavingsAccountNumber());
                        result = createCheckingAccount(account);
                    } else if (request.getAccountType() == AccountType.SAVINGS) {
                        Account account = new Account(request.getCustomerId(), request.getAccountNumber(),
                                request.getTotalBalance(), request.getAvailableBalance(),
                                Instant.now().toEpochMilli(), request.getAccountType(),
                                request.getInterestRate(), request.getMaxTransferAmountToChecking());
                        result = createSavingsAccount(account);
                    }
                } catch (Exception e) {
                    context.reply(new Response(Status.FAILED, "Failed to bind request"));
                    return;
                }

                if (result.getQueryStatus() == QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.SUCCESS, "Account created successfully"));
                    return;
                } else if (result.getQueryStatus() == QueryStatus.DUPLICATED) {
                    context.reply(new Response(Status.DUPLICATED, "Account number already exists"));
                    return;
                } else {
                    context.reply(new Response(Status.FAILED, "Failed to create account"));
                    return;
                }
            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
                return;
            }
        };
    }

    public static ContextHandler getAllAccounts() {
        return (context) -> {
            String query = "SELECT * FROM checking_account";
            QueryResult result = db.execute(query);
            List<Account> accounts = new ArrayList<>();

            if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                context.reply(new Response(Status.FAILED, "Failed to get checking accounts"));
                return;
            }

            query = "SELECT * FROM savings_account";
            QueryResult result2 = db.execute(query);

            if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                context.reply(new Response(Status.FAILED, "Failed to get savings accounts"));
                return;
            }

            while (result.next()) {
                List<Object> row = result.getCurrentRow();
                Account account = new Account((int) row.get(0), (int) row.get(1), (String) row.get(2),
                        (Long) row.get(3), (Long) row.get(4), (Long) row.get(5),
                        AccountType.CHECKING, (int) row.get(6), 0, 0L);
                accounts.add(account);
            }

            while (result2.next()) {
                List<Object> row = result2.getCurrentRow();
                Account account = new Account((int) row.get(0), (int) row.get(1), (String) row.get(2),
                        (Long) row.get(3), (Long) row.get(4), (Long) row.get(5),
                        AccountType.SAVINGS, 0, (Double) row.get(6), (Long) row.get(7));
                accounts.add(account);
            }

            context.reply(new Accounts(Status.SUCCESS, "Accounts fetched successfully", accounts));
        };
    }

    public static ContextHandler getAccount() {
        return (context) -> {
            ViewAccount request = new ViewAccount();
            if (context.bind(request)) {
                int customerId = request.getCustomerId();
                List<Account> accounts = new ArrayList<>();

                String query = "SELECT * FROM checking_account WHERE customer_id = ?";
                QueryResult result = db.execute(query, customerId);

                if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.FAILED, "Failed to get checking accounts"));
                    return;
                }

                query = "SELECT * FROM savings_account WHERE customer_id = ?";
                QueryResult result2 = db.execute(query, customerId);

                if (result2.getQueryStatus() != QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.FAILED, "Failed to get savings accounts"));
                    return;
                }

                while (result.next()) {
                    List<Object> row = result.getCurrentRow();

                    Account account = new Account((int) row.get(0), (int) row.get(1), (String) row.get(2),
                            (Long) row.get(3), (Long) row.get(4), (Long) row.get(5),
                            AccountType.CHECKING, (int) row.get(6), 0, 0L);
                    accounts.add(account);
                }

                while (result2.next()) {
                    List<Object> row = result2.getCurrentRow();

                    Account account = new Account((int) row.get(0), (int) row.get(1), (String) row.get(2),
                            (Long) row.get(3), (Long) row.get(4), (Long) row.get(5),
                            AccountType.SAVINGS, 0, (Double) row.get(6), (Long) row.get(7));
                    accounts.add(account);
                }

                context.reply(new Accounts(Status.SUCCESS, "Accounts fetched successfully", accounts));

            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }

    public static ContextHandler deleteAccount() {
        return (context) -> {
            RemoveAccount request = new RemoveAccount();
            if (context.bind(request)) {
                if (request.getAccountType() == AccountType.CHECKING) {
                    String query = "DELETE FROM checking_account WHERE id = ?";
                    QueryResult result = db.execute(query, request.getAccountId());
                    if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                        context.reply(new Response(Status.FAILED, "Failed to delete checking account"));
                        return;
                    }
                } else if (request.getAccountType() == AccountType.SAVINGS) {
                    String query = "DELETE FROM savings_account WHERE id = ?";
                    QueryResult result = db.execute(query, request.getAccountId());
                    if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                        context.reply(new Response(Status.FAILED, "Failed to delete savings account"));
                        return;
                    }
                } else {
                    context.reply(new Response(Status.FAILED, "Invalid account type"));
                    return;
                }

                context.reply(new Response(Status.SUCCESS, "Account deleted successfully"));
            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }
}
