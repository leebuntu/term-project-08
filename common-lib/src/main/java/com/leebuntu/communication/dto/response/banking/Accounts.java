package com.leebuntu.communication.dto.response.banking;


import com.leebuntu.banking.account.Account;
import com.leebuntu.communication.dto.Payload;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;

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
