package com.leebuntu.banking.dto.request.banking;

import com.leebuntu.common.communication.dto.Payload;

public class DepositWithdraw extends Payload<DepositWithdraw> {
    private String accountNumber;
    private long amount;

    public DepositWithdraw(String accountNumber, long amount) {
        super();
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public DepositWithdraw() {
        super();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public DepositWithdraw fromJson(String json) {
        DepositWithdraw depositWithdraw = super.fromJson(json);
        this.accountNumber = depositWithdraw.accountNumber;
        this.amount = depositWithdraw.amount;
        return this;
    }
}
