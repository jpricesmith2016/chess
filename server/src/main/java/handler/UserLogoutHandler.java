package handler;

import dataaccess.exceptions.DataAccessException;
import requestresult.LogoutRequest;
import requestresult.LogoutResult;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.AuthService;

import java.util.Map;

public class UserLogoutHandler implements Handler {
    final AuthService serviceAuth;
    Gson gson = new Gson();

    public UserLogoutHandler(AuthService serviceAuth) {
        this.serviceAuth = serviceAuth;
    }

    @Override
    public void handle(@NotNull Context context) {
        try {
            LogoutRequest request = new LogoutRequest(context.header("Authorization"));
            LogoutResult result = serviceAuth.logout(request);

            context.status(result.resultCode());

            context.contentType("application/json");
            if (result.resultCode() != 200) {
                context.result(gson.toJson(Map.of("message", result.message())));
            } else {
                context.result("");
            }
        } catch (DataAccessException e) {
            context.status(500);
            context.json(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
