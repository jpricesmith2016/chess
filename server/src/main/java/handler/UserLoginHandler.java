package handler;

import dataaccess.exceptions.DataAccessException;
import requestresult.LoginRequest;
import requestresult.LoginResult;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.UserService;

import java.util.Map;

public class UserLoginHandler implements Handler {

    final AuthService serviceAuth;
    final UserService serviceUser;
    final Gson gson = new Gson();

    public UserLoginHandler(AuthService serviceAuth, UserService serviceUser) {
        this.serviceAuth = serviceAuth;
        this.serviceUser = serviceUser;
    }

    @Override
    public void handle(@NotNull Context context) throws DataAccessException {
        LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);
        if (request == null) {
            context.status(400);
            context.json(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        LoginResult result = serviceAuth.login(request);
        context.status(result.resultCode());
        if (result.resultCode() != 200) {
            context.json(gson.toJson(Map.of("message", result.message())));
        } else {
            context.json(gson.toJson(Map.of("username", request.username(),
                    "authToken", result.authToken())));
        }
    }
}
