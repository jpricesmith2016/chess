package handler;

import Request_Result.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.UserService;
import Request_Result.RegisterRequest;
import Request_Result.RegisterResult;

import java.util.Map;

public class UserRegHandler implements Handler {

    UserService userService;
    AuthService authService;
    private final Gson gson = new Gson();

    public UserRegHandler (UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {

        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);
        RegisterResult result = authService.register(request);

        context.status(result.resultCode());

        String jsonResult = gson.toJson(Map.of("message", result.message()));

        if (result.resultCode() != 200) {
            context.contentType("application/json");
            context.json(jsonResult);
        } else {
            jsonResult = gson.toJson(Map.of("username", result.returnAuth().username(),
                    "authToken", result.returnAuth().authToken()));
            context.json(jsonResult);
        }
    }
}
