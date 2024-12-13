package com.leebuntu.communication.dto.request.banking;


import com.leebuntu.banking.account.AccountType;
import com.leebuntu.communication.dto.Payload;

public class RemoveAccount extends Payload<RemoveAccount> {
    private int accountId;
    private AccountType accountType;

    public RemoveAccount(int accountId, AccountType accountType) {
        super();
        this.accountId = accountId;
        this.accountType = accountType;
    }

    public RemoveAccount() {
        super();
    }

    public int getAccountId() {
        return accountId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    @Override
    public RemoveAccount fromJson(String json) {
        RemoveAccount removeAccount = super.fromJson(json);
        this.accountId = removeAccount.accountId;
        this.accountType = removeAccount.accountType;
        return this;
    }
}
