package com.leebuntu.communication.dto.request.banking;

import com.leebuntu.communication.dto.Payload;

public class DepositWithdraw extends Payload<DepositWithdraw> {
    private String accountNumber;
    private Long amount;

    public DepositWithdraw(String accountNumber, Long amount) {
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

    public Long getAmount() {
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
