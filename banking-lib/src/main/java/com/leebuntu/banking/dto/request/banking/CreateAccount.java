package com.leebuntu.banking.dto.request.banking;

import com.leebuntu.banking.account.Account;
import com.leebuntu.common.communication.dto.Payload;

public class CreateAccount extends Payload<CreateAccount> {
    private Account account;

    public CreateAccount(Account account) {
        super();
        this.account = account;
    }

    public CreateAccount() {
        super();
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public CreateAccount fromJson(String json) {
        CreateAccount createAccount = super.fromJson(json);
        this.account = createAccount.account;
        return this;
    }
}
