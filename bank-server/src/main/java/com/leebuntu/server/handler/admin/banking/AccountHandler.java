package com.leebuntu.server.handler.admin.banking;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;
import com.leebuntu.common.banking.account.Account;
import com.leebuntu.common.banking.account.AccountType;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.banking.dto.request.banking.CreateAccount;
import com.leebuntu.common.banking.dto.request.banking.RemoveAccount;
import com.leebuntu.common.banking.dto.request.banking.ViewAccount;
import com.leebuntu.common.banking.dto.response.banking.Accounts;
import com.leebuntu.server.communication.router.ContextHandler;
import com.leebuntu.server.provider.AccountProvider;
import com.leebuntu.server.provider.CustomerProvider;

import java.time.Instant;
import java.util.List;

public class AccountHandler {

    private static BankingResult createCheckingAccount(Account account) {
        if (AccountProvider.isAccountNumberExist(account.getAccountNumber())) {
            return new BankingResult(BankingResultType.DUPLICATED, "Account number already exists");
        }

        if (!account.getLinkedSavingsAccountNumber().isEmpty()
                && !AccountProvider.isAccountNumberExist(account.getLinkedSavingsAccountNumber())) {
            return new BankingResult(BankingResultType.FAILED, "Savings account id does not exist");
        }

        if (!CustomerProvider.isExistUser(account.getCustomerId())) {
            return new BankingResult(BankingResultType.NOT_FOUND, "Customer id does not exist");
        }

        return AccountProvider.createAccount(account)
                ? new BankingResult(BankingResultType.SUCCESS, "Account created successfully")
                : new BankingResult(BankingResultType.FAILED, "Failed to create account");
    }

    private static BankingResult createSavingsAccount(Account account) {
        if (AccountProvider.isAccountNumberExist(account.getAccountNumber())) {
            return new BankingResult(BankingResultType.DUPLICATED, "Account number already exists");
        }

        if (!CustomerProvider.isExistUser(account.getCustomerId())) {
            return new BankingResult(BankingResultType.NOT_FOUND, "Customer id does not exist");
        }

        return AccountProvider.createAccount(account)
                ? new BankingResult(BankingResultType.SUCCESS, "Account created successfully")
                : new BankingResult(BankingResultType.FAILED, "Failed to create account");
    }

    public static ContextHandler createAccount() {
        return (context) -> {
            CreateAccount request = new CreateAccount();

            if (context.bind(request)) {
                BankingResult result = null;

                try {
                    if (request.getAccount().getAccountType() == AccountType.CHECKING) {
                        Account account = new Account();
                        account.setCustomerId(request.getAccount().getCustomerId());
                        account.setAccountNumber(request.getAccount().getAccountNumber());
                        account.setTotalBalance(request.getAccount().getTotalBalance());
                        account.setAvailableBalance(request.getAccount().getAvailableBalance());
                        account.setOpenDate(Instant.now().toEpochMilli());
                        account.setAccountType(request.getAccount().getAccountType());
                        account.setLinkedSavingsAccountNumber(request.getAccount().getLinkedSavingsAccountNumber());
                        result = createCheckingAccount(account);
                    } else if (request.getAccount().getAccountType() == AccountType.SAVINGS) {
                        Account account = new Account();
                        account.setCustomerId(request.getAccount().getCustomerId());
                        account.setAccountNumber(request.getAccount().getAccountNumber());
                        account.setTotalBalance(request.getAccount().getTotalBalance());
                        account.setAvailableBalance(request.getAccount().getAvailableBalance());
                        account.setOpenDate(Instant.now().toEpochMilli());
                        account.setAccountType(request.getAccount().getAccountType());
                        account.setInterestRate(request.getAccount().getInterestRate());
                        account.setMaxTransferAmountToChecking(request.getAccount().getMaxTransferAmountToChecking());
                        result = createSavingsAccount(account);
                    }

                    if (result.getType() == BankingResultType.SUCCESS) {
                        context.reply(new Response(Status.SUCCESS, result.getFineMessage()));
                    } else {
                        context.reply(new Response(Status.FAILED, result.getFineMessage()));
                    }
                } catch (Exception e) {
                    context.reply(new Response(Status.FAILED, "Failed to bind request"));
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
            List<Account> accounts = AccountProvider.getAllAccounts();
            if (accounts.isEmpty()) {
                context.reply(new Response(Status.FAILED, "No accounts found"));
                return;
            }

            context.reply(new Accounts(Status.SUCCESS, "Accounts fetched successfully", accounts));
        };
    }

    public static ContextHandler getAccount() {
        return (context) -> {
            ViewAccount request = new ViewAccount();
            if (context.bind(request)) {
                int customerId = request.getCustomerId();
                List<Account> accounts = AccountProvider.getAccounts(customerId);
                if (accounts.isEmpty()) {
                    context.reply(new Response(Status.FAILED, "No accounts found"));
                    return;
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
                if (AccountProvider.deleteAccount(request.getAccountNumber())) {
                    context.reply(new Response(Status.SUCCESS, "Account deleted successfully"));
                } else {
                    context.reply(new Response(Status.FAILED, "Failed to delete account"));
                }
            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }
}
