package handler;

import Request_Result.GameJoinRequest;
import Request_Result.LoginRequest;
import Request_Result.LoginResult;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.UserService;

import java.util.Map;

public class UserLoginHandler implements Handler {

    AuthService serviceAuth;
    UserService serviceUser;
    Gson gson = new Gson();

    public UserLoginHandler(AuthService serviceAuth, UserService serviceUser) {
        this.serviceAuth = serviceAuth;
        this.serviceUser = serviceUser;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);
        LoginResult result = serviceAuth.login(request);
        context.status(result.resultCode());
        if (result.resultCode() != 200) {
            context.json(Map.of("message", result.message()));
        } else {
            context.json(Map.of("username", request.username(),
                    "authToken", result.authToken()));
        }
    }
}
