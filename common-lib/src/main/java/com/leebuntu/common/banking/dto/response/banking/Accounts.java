package com.leebuntu.common.banking.dto.response.banking;


import com.leebuntu.common.banking.account.Account;
import com.leebuntu.common.communication.dto.Payload;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;

import java.util.ArrayList;
import java.util.List;

public class Accounts extends Payload<Accounts> {
    private Response response;
    private List<Account> accounts;

    public Accounts(Status status, String message, List<Account> accounts) {
        super();
        this.response = new Response(status, message);
        this.accounts = accounts;
    }

    public Accounts() {
        super();
        this.response = new Response();
        this.accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public Accounts fromJson(String json) {
        Accounts accounts = super.fromJson(json);
        this.response = accounts.response;
        this.accounts = accounts.accounts;
        return this;
    }
}
