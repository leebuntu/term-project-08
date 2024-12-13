package com.leebuntu.communication.jwt;

import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.router.Middleware;

public class JWTMiddleware {
    public static Middleware getJWTMiddleware() {
        return (context) -> {
            String token = context.getAuthToken();
            if (token == null) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return false;
            }

            int userId = JWTManager.validateToken(token);
            if (userId == -1) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return false;
            }
            context.setField("userId", userId);
            return true;
        };
    }
}