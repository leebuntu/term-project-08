package com.leebuntu.communication.dto.request.banking;

import com.leebuntu.communication.dto.Payload;

public class ViewAccount extends Payload<ViewAccount> {
    private int customerId;

    public ViewAccount(int customerId) {
        super();
        this.customerId = customerId;
    }

    public ViewAccount() {
        super();
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public ViewAccount fromJson(String json) {
        ViewAccount viewAccount = super.fromJson(json);
        this.customerId = viewAccount.customerId;
        return this;
    }
}
