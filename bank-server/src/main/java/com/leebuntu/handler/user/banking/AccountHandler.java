package com.leebuntu.handler.user.banking;

import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.account.AccountType;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.dto.request.banking.DepositWithdraw;
import com.leebuntu.communication.dto.request.banking.Transfer;
import com.leebuntu.communication.dto.response.banking.Accounts;
import com.leebuntu.communication.router.ContextHandler;
import com.leebuntu.db.core.Database;
import com.leebuntu.db.core.DatabaseManager;
import com.leebuntu.db.query.QueryResult;
import com.leebuntu.db.query.enums.QueryStatus;
import com.leebuntu.handler.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AccountHandler {
    private static final Database accountDB = DatabaseManager.getDB("accounts");

    public static ContextHandler withdraw() { // 초반 7일은 withdraw 불가능
        return (context) -> {
            int userId = (int) context.getField("userId");

            DepositWithdraw request = new DepositWithdraw();
            if (context.bind(request)) {
                int accountId = 0;
                Long totalBalance = 0L;
                Long availableBalance = 0L;

                AccountType accountType = Utils.getAccountType(request.getAccountNumber());
                String accountTypeString = accountType.name().toLowerCase();

                String query = "SELECT id, customer_id, total_balance, available_balance FROM "
                        + accountTypeString + "_account WHERE account_number = ?";
                QueryResult result = accountDB.execute(query, request.getAccountNumber());

                if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.FAILED,
                            "Failed to get " + accountTypeString + " account"));
                    return;
                }

                if ((int) result.getCurrentRow().get(1) == userId) {
                    accountId = (int) result.getCurrentRow().get(0);
                    totalBalance = (Long) result.getCurrentRow().get(2);
                    availableBalance = (Long) result.getCurrentRow().get(3);
                }

                if (accountId == 0) {
                    context.reply(new Response(Status.FAILED, "Account not found"));
                    return;
                }

                if (availableBalance < request.getAmount()) {
                    context.reply(new Response(Status.FAILED, "Insufficient funds"));
                    return;
                }

                if (totalBalance < request.getAmount()) {
                    context.reply(new Response(Status.FAILED, "Insufficient funds"));
                    return;
                }

                totalBalance -= request.getAmount();

                query = "UPDATE FROM " + accountTypeString + "_account total_balance = ? WHERE id = ?";
                QueryResult result2 = accountDB.execute(query, totalBalance, accountId);

                if (result2.getQueryStatus() != QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.FAILED, "Failed to update " + accountTypeString + " account"));
                    return;
                }

                context.reply(new Response(Status.SUCCESS, "Withdrawal successful"));
            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }

    public static ContextHandler deposit() {
        return (context) -> {
            int userId = (int) context.getField("userId");

            DepositWithdraw request = new DepositWithdraw();
            if (context.bind(request)) {
                int accountId = 0;
                Long totalBalance = 0L;

                AccountType accountType = Utils.getAccountType(request.getAccountNumber());
                String accountTypeString = accountType.name().toLowerCase();

                String query = "SELECT id, customer_id, total_balance FROM "
                        + accountTypeString + "_account WHERE account_number = ?";
                QueryResult result = accountDB.execute(query, request.getAccountNumber());

                if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.FAILED, "Failed to get " + accountTypeString + " account"));
                    return;
                }

                if ((int) result.getCurrentRow().get(1) == userId) {
                    accountId = (int) result.getCurrentRow().get(0);
                    totalBalance = (Long) result.getCurrentRow().get(2);
                }

                totalBalance += request.getAmount();

                query = "UPDATE FROM " + accountTypeString + "_account total_balance = ? WHERE id = ?";
                QueryResult result2 = accountDB.execute(query, totalBalance, accountId);

                if (result2.getQueryStatus() != QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.FAILED, "Failed to update " + accountTypeString + " account"));
                    return;
                }

                context.reply(new Response(Status.SUCCESS, "Deposit successful"));
            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }

    private static int isMoneySufficient(Long totalBalance, Long availableBalance, Long amount,
            int linkedSavingAccountId) {
        if (totalBalance >= amount && availableBalance >= amount) {
            return -2;
        } else {
            if (linkedSavingAccountId > -1) {
                String query = "SELECT total_balance, max_transfer_amount_to_checking FROM savings_account WHERE id = ?";
                QueryResult result = accountDB.execute(query, linkedSavingAccountId);

                if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                    return -3;
                }

                Long savingTotalBalance = (Long) result.getCurrentRow().get(0);
                Long maxTransferAmountToChecking = (Long) result.getCurrentRow().get(1);

                if (savingTotalBalance >= amount && maxTransferAmountToChecking >= amount) {
                    return linkedSavingAccountId;
                }
            }
        }

        return -3;

    }

    private static boolean updateAccount(int accountId, int receiverAccountId, Long amount) {
        String query = "SELECT total_balance, available_balance, linked_savings_id FROM checking_account WHERE id = ?";
        QueryResult result = accountDB.execute(query, accountId);

        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return false;
        }

        Long totalBalance = (Long) result.getCurrentRow().get(0);
        Long availableBalance = (Long) result.getCurrentRow().get(1);
        int linkedSavingsId = (int) result.getCurrentRow().get(2);

        query = "SELECT total_balance FROM checking_account WHERE id = ?";
        result = accountDB.execute(query, receiverAccountId);

        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return false;
        }

        Long receiverTotalBalance = (Long) result.getCurrentRow().get(0);

        int moneyStatus = isMoneySufficient(totalBalance, availableBalance, amount, linkedSavingsId);

        if (moneyStatus == -2) {
            totalBalance -= amount;
            receiverTotalBalance += amount;
        } else if (moneyStatus > -1) {
            Long remainingAmount = amount - totalBalance;
            totalBalance = 0L;
            receiverTotalBalance += amount;

            query = "UPDATE FROM checking_account total_balance = ? WHERE id = ?";
            result = accountDB.execute(query, totalBalance, accountId);

            if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                return false;
            }

            query = "UPDATE FROM checking_account total_balance = ? WHERE id = ?";
            result = accountDB.execute(query, receiverTotalBalance, receiverAccountId);

            if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                return false;
            }

            query = "SELECT total_balance FROM savings_accout WHERE id = ?";
            result = accountDB.execute(query, moneyStatus);

            if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                return false;
            }

            Long savingTotalBalance = (Long) result.getCurrentRow().get(0);
            savingTotalBalance -= remainingAmount;

            query = "UPDATE FROM savings_account total_balance = ? WHERE id = ?";
            result = accountDB.execute(query, savingTotalBalance, moneyStatus);

            if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public static ContextHandler transfer() {
        return (context) -> {
            int userId = (int) context.getField("userId");
            Transfer request = new Transfer();
            if (context.bind(request)) {
                if (Utils.isAccountNumberExist(request.getAccountNumber())
                        && Utils.isAccountNumberExist(request.getReceiverAccountNumber())) {
                    AccountType accountType = Utils.getAccountType(request.getAccountNumber());
                    AccountType receiverAccountType = Utils.getAccountType(request.getReceiverAccountNumber());

                    if (accountType == AccountType.CHECKING && receiverAccountType == AccountType.CHECKING) {
                        String query = "SELECT id, customer_id FROM checking_account WHERE account_number = ?";
                        QueryResult result = accountDB.execute(query, request.getAccountNumber());

                        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
                            context.reply(new Response(Status.FAILED, "Failed to get checking account"));
                            return;
                        }

                        int senderAccountId = (int) result.getCurrentRow().get(0);
                        int customerId = (int) result.getCurrentRow().get(1);

                        if (customerId != userId) {
                            context.reply(new Response(Status.NOT_AUTHORIZED, "Not Authorized"));
                            return;
                        }

                        query = "SELECT id FROM checking_account WHERE account_number = ?";
                        QueryResult result2 = accountDB.execute(query, request.getReceiverAccountNumber());

                        if (result2.getQueryStatus() != QueryStatus.SUCCESS) {
                            context.reply(new Response(Status.FAILED, "Failed to get receiver checking account"));
                            return;
                        }

                        int receiverAccountId = (int) result2.getCurrentRow().get(0);

                        if (updateAccount(senderAccountId, receiverAccountId, request.getAmount())) {
                            context.reply(new Response(Status.SUCCESS, "Transfer successful"));
                        } else {
                            context.reply(new Response(Status.FAILED, "Failed to update accounts"));
                        }

                        context.reply(new Response(Status.SUCCESS, "Transfer successful"));
                    } else {
                        context.reply(new Response(Status.FAILED, "Cannot trnasfer with savings account"));
                        return;
                    }
                } else {
                    context.reply(new Response(Status.FAILED, "Account not found"));
                    return;
                }
            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
                return;
            }
        };

    }

    public static ContextHandler getAccounts() {
        return (context) -> {
            int userId = (int) context.getField("userId");

            List<Account> accounts = new ArrayList<>();

            String query = "SELECT account_number, total_balance, available_balance FROM checking_account WHERE customer_id = ?";
            QueryResult result = accountDB.execute(query, userId);

            if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
                context.reply(new Response(Status.FAILED, "Failed to get checking accounts"));
                return;
            }

            while (result.next()) {
                List<Object> row = result.getCurrentRow();
                Account account = new Account((String) row.get(0), (Long) row.get(1), (Long) row.get(2),
                        AccountType.CHECKING);
                accounts.add(account);
            }

            query = "SELECT account_number, total_balance, available_balance FROM savings_account WHERE customer_id = ?";
            QueryResult result2 = accountDB.execute(query, userId);

            if (result2.getQueryStatus() != QueryStatus.SUCCESS && result2.getQueryStatus() != QueryStatus.NOT_FOUND) {
                context.reply(new Response(Status.FAILED, "Failed to get savings accounts"));
                return;
            }

            while (result2.next()) {
                List<Object> row = result2.getCurrentRow();
                Account account = new Account((String) row.get(0), (Long) row.get(1), (Long) row.get(2),
                        AccountType.SAVINGS);
                accounts.add(account);
            }

            context.reply(new Accounts(Status.SUCCESS, "Accounts fetched successfully", accounts));
        };
    }
}