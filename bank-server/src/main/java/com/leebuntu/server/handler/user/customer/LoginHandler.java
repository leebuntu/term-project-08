package com.leebuntu.server.handler.user.customer;

import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.communication.dto.request.auth.Login;
import com.leebuntu.common.communication.jwt.JWTManager;
import com.leebuntu.common.communication.router.ContextHandler;
import com.leebuntu.provider.CustomerProvider;

public class LoginHandler {
    public static ContextHandler getLoginHandler() {
        return (context) -> {
            Login login = new Login();

            if (context.bind(login)) {
                int userId = CustomerProvider.login(login.getCustomerId(), login.getPassword());
                if (userId == -1) {
                    context.reply(new Response(Status.NOT_FOUND, "ID or password incorrect"));
                } else {
                    String token = JWTManager.createJWT(userId);
                    context.reply(new Response(Status.SUCCESS, token));
                }
            } else {
                context.reply(new Response(Status.INVALID_REQUEST, "Invalid login request"));
            }
        };
    }
}
