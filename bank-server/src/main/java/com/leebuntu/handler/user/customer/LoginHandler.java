package com.leebuntu.handler.user.customer;

import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.dto.request.auth.Login;
import com.leebuntu.communication.jwt.JWTManager;
import com.leebuntu.communication.router.ContextHandler;
import com.leebuntu.db.core.Database;
import com.leebuntu.db.core.DatabaseManager;
import com.leebuntu.db.query.QueryResult;

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
