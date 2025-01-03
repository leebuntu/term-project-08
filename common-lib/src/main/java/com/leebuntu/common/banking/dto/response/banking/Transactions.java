package com.leebuntu.common.banking.dto.response.banking;

import com.leebuntu.common.banking.Transaction;
import com.leebuntu.common.communication.dto.Payload;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;

import java.util.ArrayList;
import java.util.List;

public class Transactions extends Payload<Transactions> {
    private Response response;
    private List<Transaction> transactions;

    public Transactions(Status status, String message, List<Transaction> transactions) {
        super();
        this.response = new Response(status, message);
        this.transactions = transactions;
    }

    public Transactions() {
        super();
        this.response = new Response();
        this.transactions = new ArrayList<>();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public Transactions fromJson(String json) {
        Transactions transactions = super.fromJson(json);
        this.response = transactions.response;
        this.transactions = transactions.transactions;
        return this;
    }

}
