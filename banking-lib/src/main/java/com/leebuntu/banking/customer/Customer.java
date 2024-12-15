package com.leebuntu.banking.customer;

public class Customer {
    private int id;
    private String name;
    private String customerId;
    private String password;
    private String address;
    private String phone;
    private int creditScore;

    public Customer() {
    }

    public Customer(int id, String name, String customerId, String password, String address, String phone,
            int creditScore) {
        this.id = id;
        this.name = name;
        this.customerId = customerId;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.creditScore = creditScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }
}
