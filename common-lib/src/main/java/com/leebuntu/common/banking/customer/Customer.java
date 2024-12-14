package com.leebuntu.common.banking.customer;

public class Customer {
    private int id;
    private String name;
    private String customerId;
    private String address;
    private String phone;
    private int creditScore;

    public Customer() {
    }

    public Customer(int id, String name, String customerId, String address, String phone, int creditScore) {
        this.id = id;
        this.name = name;
        this.customerId = customerId;
        this.address = address;
        this.phone = phone;
        this.creditScore = creditScore;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public int getCreditScore() {
        return creditScore;
    }
}
