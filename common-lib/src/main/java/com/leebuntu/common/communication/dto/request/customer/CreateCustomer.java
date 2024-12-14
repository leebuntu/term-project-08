package com.leebuntu.common.communication.dto.request.customer;

import com.leebuntu.common.communication.dto.Payload;

public class CreateCustomer extends Payload<CreateCustomer> {
    private String customerId;
    private String name;
    private String password;
    private String address;
    private String phone;

    public CreateCustomer(String customerId, String name, String password, String address, String phone) {
        super();
        this.customerId = customerId;
        this.name = name;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }

    public CreateCustomer() {
        super();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public CreateCustomer fromJson(String json) {
        CreateCustomer createCustomer = super.fromJson(json);
        this.customerId = createCustomer.customerId;
        this.name = createCustomer.name;
        this.password = createCustomer.password;
        this.address = createCustomer.address;
        this.phone = createCustomer.phone;
        return this;
    }
}
