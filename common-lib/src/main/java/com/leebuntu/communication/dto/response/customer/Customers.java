package com.leebuntu.communication.dto.response.customer;


import com.leebuntu.banking.customer.Customer;
import com.leebuntu.communication.dto.Payload;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;

import java.util.ArrayList;
import java.util.List;

public class Customers extends Payload<Customers> {
    private Response response;
    private List<Customer> customers;

    public Customers(Status status, String message, List<Customer> customers) {
        super();
        this.response = new Response(status, message);
        this.customers = customers;
    }

    public Customers() {
        super();
        this.response = new Response();
        this.customers = new ArrayList<>();
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    @Override
    public Customers fromJson(String json) {
        Customers customers = super.fromJson(json);
        this.response = customers.response;
        this.customers = customers.customers;
        return this;
    }
}
