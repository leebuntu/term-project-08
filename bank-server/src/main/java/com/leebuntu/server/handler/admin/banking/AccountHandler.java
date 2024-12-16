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
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;
import com.leebuntu.server.provider.AccountProvider;
import com.leebuntu.server.provider.CustomerProvider;

import java.time.Instant;
import java.util.List;

public class AccountHandler {
    private static final Database accountDB = DatabaseManager.getDB("accounts");

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
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            CreateAccount request = new CreateAccount();

            if (context.bind(request)) {
                Account account = request.getAccount();
                if (!account.validate()) {
                    context.reply(new Response(Status.FAILED, "Invalid account data"));
                    return;
                }

                BankingResult result = null;

                try {
                    if (account.getAccountType() == AccountType.CHECKING) {
                        result = createCheckingAccount(account);
                    } else if (account.getAccountType() == AccountType.SAVINGS) {
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
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }
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
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

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
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            RemoveAccount request = new RemoveAccount();
            if (context.bind(request)) {
                AccountType accountType = AccountProvider.getAccountType(request.getAccountNumber());

                switch (accountType) {
                    case CHECKING:
                        if (AccountProvider.deleteAccount(request.getAccountNumber())) {
                            context.reply(new Response(Status.SUCCESS, "Account deleted successfully"));
                        } else {
                            context.reply(new Response(Status.FAILED, "Failed to delete account"));
                        }
                        break;
                    case SAVINGS:
                        accountDB.beginTransaction();
                        List<Account> linkedCheckingAccounts = AccountProvider
                                .getAccountByLinkedSavingsAccountNumber(request.getAccountNumber());
                        for (Account linkedCheckingAccount : linkedCheckingAccounts) {
                            linkedCheckingAccount.setLinkedSavingsAccountNumber(null);
                            AccountProvider.updateAccount(linkedCheckingAccount);
                        }

                        if (AccountProvider.deleteAccount(request.getAccountNumber())) {
                            accountDB.endTransaction();
                            context.reply(new Response(Status.SUCCESS, "Account deleted successfully"));
                        } else {
                            accountDB.endTransaction();
                            context.reply(new Response(Status.FAILED, "Failed to delete account"));
                        }

                        break;
                }

            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }

    public static ContextHandler updateAccount() {
        return (context) -> {
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            CreateAccount request = new CreateAccount();

            if (context.bind(request)) {
                Account account = request.getAccount();
                if (account.getAccountNumber() != null && !account.getAccountNumber().isEmpty()) {
                    account.setAccountType(AccountProvider.getAccountType(account.getAccountNumber()));
                }

                if (!account.validate()) {
                    context.reply(new Response(Status.FAILED, "Invalid account data"));
                    return;
                }

                if (account.getAccountType() == AccountType.CHECKING) {
                    if (!account.getLinkedSavingsAccountNumber().isEmpty()) {
                        if (!AccountProvider.isAccountNumberExist(account.getLinkedSavingsAccountNumber())
                                || !AccountProvider.isAccountOwner(account.getLinkedSavingsAccountNumber(),
                                        account.getId())) {
                            context.reply(new Response(Status.FAILED, "Savings account does not exist"));
                            return;
                        }
                    }
                }

                try {
                    boolean result = AccountProvider.updateAccount(account);

                    if (result) {
                        context.reply(new Response(Status.SUCCESS, "Account updated successfully"));
                    } else {
                        context.reply(new Response(Status.FAILED, "Failed to update account"));
                    }
                } catch (Exception e) {
                    context.reply(new Response(Status.FAILED, "Failed to bind request"));
                    return;
                }

            } else {
                context.reply(new Response(Status.FAILED, "Failed to bind request"));
            }
        };
    }
}
