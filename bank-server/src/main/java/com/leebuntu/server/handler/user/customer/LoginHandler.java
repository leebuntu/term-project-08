package com.leebuntu.server.handler.user.customer;

import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.communication.dto.request.auth.Login;
import com.leebuntu.common.communication.jwt.JWTManager;
import com.leebuntu.common.communication.router.ContextHandler;
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;
import com.leebuntu.server.db.query.QueryResult;

public class LoginHandler {
    private static Database db = DatabaseManager.getDB("customers");

    public static ContextHandler getLoginHandler() {
        return (context) -> {
            Login login = new Login();

            if (context.bind(login)) {
                String query = "SELECT id, password FROM user WHERE customer_id = ?";
                QueryResult result = db.execute(query, login.getCustomerId());

                if (result.getRowCount() == 0) {
                    context.reply(new Response(Status.NOT_FOUND, "User not found"));
                } else {
                    String password = result.getCurrentRow().get(1).toString();
                    if (password.equals(login.getPassword())) {
                        int userId = Integer.parseInt(result.getCurrentRow().get(0).toString());
                        String token = JWTManager.createJWT(userId);
                        context.reply(new Response(Status.SUCCESS, token));
                    } else {
                        context.reply(new Response(Status.FAILED, "Password incorrect"));
                    }
                }
            } else {
                context.reply(new Response(Status.INVALID_REQUEST, "Invalid login request"));
            }
        };
    }
}
