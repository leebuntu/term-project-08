package com.leebuntu.communication.dto.request.customer;

import com.leebuntu.communication.dto.Payload;

public class RemoveCustomer extends Payload<RemoveCustomer> {

    private int id;

    public RemoveCustomer(int id) {
        super();
        this.id = id;
    }

    public RemoveCustomer() {
        super();
    }

    public int getId() {
        return id;
    }

    @Override
    public RemoveCustomer fromJson(String json) {
        RemoveCustomer removeCustomer = super.fromJson(json);
        this.id = removeCustomer.id;
        return this;
    }
}
