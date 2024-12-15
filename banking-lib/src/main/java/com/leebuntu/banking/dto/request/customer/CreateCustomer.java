package com.leebuntu.banking.dto.request.customer;

import com.leebuntu.banking.customer.Customer;
import com.leebuntu.common.communication.dto.Payload;

public class CreateCustomer extends Payload<CreateCustomer> {
    private Customer customer;

    public CreateCustomer(Customer customer) {
        super();
        this.customer = customer;
    }

    public CreateCustomer() {
        super();
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public CreateCustomer fromJson(String json) {
        CreateCustomer createCustomer = super.fromJson(json);
        this.customer = createCustomer.customer;
        return this;
    }
}
