package com.leebuntu.server.handler.user.banking;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;
import com.leebuntu.banking.Transaction;
import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.account.AccountType;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.banking.dto.request.banking.DepositWithdraw;
import com.leebuntu.banking.dto.request.banking.Transfer;
import com.leebuntu.banking.dto.response.banking.Accounts;
import com.leebuntu.common.communication.router.ContextHandler;
import com.leebuntu.server.provider.AccountProvider;
import com.leebuntu.server.provider.TransactionProvider;
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;

import java.time.Instant;
import java.util.List;

public class AccountHandler {
    private static final Database accountDB = DatabaseManager.getDB("accounts");

    public static ContextHandler withdraw() {
        return (context) -> {
            int userId = (int) context.getField("userId");

            DepositWithdraw request = new DepositWithdraw();
            if (context.bind(request)) {
                if (!AccountProvider.isAcountOwner(request.getAccountNumber(), userId)) {
                    context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                    return;
                }

                Account account = AccountProvider.getAccount(request.getAccountNumber());
                if (account == null) {
                    context.reply(new Response(Status.FAILED, "Account not found"));
                    return;
                }

                if (account.getTotalBalance() >= request.getAmount()
                        && account.getAvailableBalance() >= request.getAmount()) {
                    if (AccountProvider.updateAccountBalance(request.getAccountNumber(),
                            account.getTotalBalance() - request.getAmount())) {

                        Transaction transaction = new Transaction(userId, -1, request.getAccountNumber(), "ATM",
                                request.getAmount(), Instant.now().toEpochMilli());
                        TransactionProvider.addTransaction(transaction);

                        context.reply(new Response(Status.SUCCESS, "Withdraw successful"));
                    } else {
                        context.reply(new Response(Status.FAILED, "Failed to update account balance"));
                    }
                } else {
                    context.reply(new Response(Status.FAILED, "Insufficient funds"));
                }
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
                if (!AccountProvider.isAcountOwner(request.getAccountNumber(), userId)) {
                    context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                    return;
                }

                Account account = AccountProvider.getAccount(request.getAccountNumber());
                if (account == null) {
                    context.reply(new Response(Status.FAILED, "Account not found"));
                    return;
                }

                if (AccountProvider.updateAccountBalance(request.getAccountNumber(),
                        account.getTotalBalance() + request.getAmount())) {

                    Transaction transaction = new Transaction(-1, userId, "ATM", request.getAccountNumber(),
                            request.getAmount(), Instant.now().toEpochMilli());
                    TransactionProvider.addTransaction(transaction);

                    context.reply(new Response(Status.SUCCESS, "Deposit successful"));
                } else {
                    context.reply(new Response(Status.FAILED, "Failed to update account balance"));
                }
            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }

    private static BankingResult checkMoneySufficient(Account account, Account receiverAccount, Long amount) {
        if (account.getTotalBalance() >= amount && account.getAvailableBalance() >= amount) {
            return new BankingResult(BankingResultType.SUCCESS, "Money is sufficient");
        } else {
            if (account.getLinkedSavingsAccountNumber() != null) {
                Account savingsAccount = AccountProvider.getAccount(account.getLinkedSavingsAccountNumber());
                if (savingsAccount.getTotalBalance() >= amount
                        && savingsAccount.getMaxTransferAmountToChecking() >= amount) {
                    return new BankingResult(BankingResultType.NEED_TO_SAVINGS_ACCOUNT, "Money is sufficient",
                            account.getLinkedSavingsAccountNumber());
                }
            }
        }

        return new BankingResult(BankingResultType.FAILED, "Money is insufficient");
    }

    private static BankingResult updateAccount(Account account, Account receiverAccount, Long amount) {
        BankingResult moneyStatus = checkMoneySufficient(account, receiverAccount, amount);

        if (moneyStatus.getType() == BankingResultType.SUCCESS) {
            account.setTotalBalance(account.getTotalBalance() - amount);
            receiverAccount.setTotalBalance(receiverAccount.getTotalBalance() + amount);

            if (AccountProvider.updateAccountBalance(account.getAccountNumber(), account.getTotalBalance()) &&
                    AccountProvider.updateAccountBalance(receiverAccount.getAccountNumber(),
                            receiverAccount.getTotalBalance())) {
                Long currentTime = Instant.now().toEpochMilli();
                Transaction transaction = new Transaction(account.getCustomerId(), receiverAccount.getCustomerId(),
                        account.getAccountNumber(), receiverAccount.getAccountNumber(), amount,
                        currentTime);
                TransactionProvider.addTransaction(transaction);

                return new BankingResult(BankingResultType.SUCCESS, "Transfer successful");
            }
        } else if (moneyStatus.getType() == BankingResultType.NEED_TO_SAVINGS_ACCOUNT) {
            Account savingsAccount = AccountProvider.getAccount(account.getLinkedSavingsAccountNumber());

            Long canTransferAmount = Math.min(account.getTotalBalance(), account.getAvailableBalance());
            Long remainingAmount = amount - canTransferAmount;

            account.setTotalBalance(0L);
            savingsAccount.setTotalBalance(savingsAccount.getTotalBalance() - remainingAmount);
            receiverAccount.setTotalBalance(receiverAccount.getTotalBalance() + amount);

            if (AccountProvider.updateAccountBalance(account.getAccountNumber(), account.getTotalBalance()) &&
                    AccountProvider.updateAccountBalance(savingsAccount.getAccountNumber(),
                            savingsAccount.getTotalBalance())
                    &&
                    AccountProvider.updateAccountBalance(receiverAccount.getAccountNumber(),
                            receiverAccount.getTotalBalance())) {
                Long currentTime = Instant.now().toEpochMilli();
                Transaction transaction = new Transaction(account.getCustomerId(),
                        receiverAccount.getCustomerId(),
                        account.getAccountNumber(), receiverAccount.getAccountNumber(), amount,
                        currentTime);
                TransactionProvider.addTransaction(transaction);

                Transaction autoTransferTransaction = new Transaction(savingsAccount.getCustomerId(),
                        account.getCustomerId(),
                        savingsAccount.getAccountNumber(), account.getAccountNumber(), remainingAmount,
                        currentTime);
                TransactionProvider.addTransaction(autoTransferTransaction);

                return new BankingResult(BankingResultType.SUCCESS, "Transfer successful");
            }
        } else {
            return new BankingResult(BankingResultType.FAILED, "Money is insufficient");
        }

        return new BankingResult(BankingResultType.FAILED, "Failed to update accounts");
    }

    public static ContextHandler transfer() {
        return (context) -> {
            int userId = (int) context.getField("userId");
            Transfer request = new Transfer();
            if (context.bind(request)) {
                if (!AccountProvider.isAccountNumberExist(request.getAccountNumber())
                        || !AccountProvider.isAccountNumberExist(request.getReceiverAccountNumber())) {
                    context.reply(new Response(Status.FAILED, "Account not found"));
                    return;
                }

                Account senderAccount = AccountProvider.getAccount(request.getAccountNumber());
                Account receiverAccount = AccountProvider.getAccount(request.getReceiverAccountNumber());

                if (senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())) {
                    context.reply(new Response(Status.FAILED, "Cannot transfer to self"));
                    return;
                }

                if (senderAccount.getAccountType() != AccountType.CHECKING
                        || receiverAccount.getAccountType() != AccountType.CHECKING) {
                    context.reply(new Response(Status.FAILED, "Cannot transfer with savings account"));
                    return;
                }

                if (senderAccount.getCustomerId() != userId) {
                    context.reply(new Response(Status.NOT_AUTHORIZED, "Not Authorized"));
                    return;
                }

                accountDB.beginTransaction();

                BankingResult bankingResult = updateAccount(senderAccount, receiverAccount,
                        request.getAmount());

                accountDB.endTransaction();

                if (bankingResult.getType() == BankingResultType.SUCCESS) {
                    context.reply(new Response(Status.SUCCESS, "Transfer successful"));
                } else {
                    context.reply(new Response(Status.FAILED, bankingResult.getMessage()));
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

            List<Account> accounts = AccountProvider.getAccounts(userId);
            if (accounts.isEmpty()) {
                context.reply(new Response(Status.FAILED, "No accounts found"));
                return;
            }

            context.reply(new Accounts(Status.SUCCESS, "Accounts fetched successfully", accounts));
        };
    }
}
