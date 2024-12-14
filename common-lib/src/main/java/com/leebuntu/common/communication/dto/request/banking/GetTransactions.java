package com.leebuntu.common.communication.dto.request.banking;

import com.leebuntu.common.communication.dto.Payload;

public class GetTransactions extends Payload<GetTransactions> {
    private String accountNumber;

    public GetTransactions(String accountNumber) {
        super();
        this.accountNumber = accountNumber;
    }

    public GetTransactions() {
        super();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public GetTransactions fromJson(String json) {
        GetTransactions getTransactions = super.fromJson(json);
        this.accountNumber = getTransactions.accountNumber;
        return this;
    }
}
