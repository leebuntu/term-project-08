package com.leebuntu.common.banking.dto.request.banking;

import com.leebuntu.common.communication.dto.Payload;

public class RemoveAccount extends Payload<RemoveAccount> {
    private String accountNumber;

    public RemoveAccount(String accountNumber) {
        super();
        this.accountNumber = accountNumber;
    }

    public RemoveAccount() {
        super();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public RemoveAccount fromJson(String json) {
        RemoveAccount removeAccount = super.fromJson(json);
        this.accountNumber = removeAccount.accountNumber;
        return this;
    }
}
