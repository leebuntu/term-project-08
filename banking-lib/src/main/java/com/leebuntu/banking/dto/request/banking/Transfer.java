package com.leebuntu.banking.dto.request.banking;

import com.leebuntu.common.communication.dto.Payload;

public class Transfer extends Payload<Transfer> {
    private String accountNumber;
    private Long amount;
    private String receiverAccountNumber;

    public Transfer(String accountNumber, Long amount, String receiverAccountNumber) {
        super();
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public Transfer() {
        super();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Long getAmount() {
        return amount;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    @Override
    public Transfer fromJson(String json) {
        Transfer transfer = super.fromJson(json);
        this.accountNumber = transfer.accountNumber;
        this.amount = transfer.amount;
        this.receiverAccountNumber = transfer.receiverAccountNumber;
        return this;
    }
}
