package handler;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.UserService;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;

import java.util.Map;

public class UserRegHandler implements Handler {

    final UserService userService;
    final AuthService authService;
    private final Gson gson = new Gson();

    public UserRegHandler (UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public void handle(@NotNull Context context) {
        try {
            RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);
            if (request == null) {
                context.status(400);
                context.json(Map.of("message", "Error: bad request"));
                return;
            }
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
        } catch (DataAccessException e) {
            context.status(500);
            context.json(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
