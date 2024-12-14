package com.leebuntu.common.communication.dto.request.auth;

import com.leebuntu.common.communication.dto.Payload;

public class Login extends Payload<Login> {
    private String customerId;
    private String password;

    public Login(String customerId, String password) {
        super();
        this.customerId = customerId;
        this.password = password;
    }

    public Login() {
        super();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Login fromJson(String json) {
        Login login = super.fromJson(json);
        this.customerId = login.customerId;
        this.password = login.password;
        return this;
    }

}
