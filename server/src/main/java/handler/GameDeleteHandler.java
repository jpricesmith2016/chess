package handler;

import dataaccess.exceptions.DataAccessException;
import requestresult.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.AuthService;

import java.util.Map;

public class GameDeleteHandler implements Handler {
    final GameService serviceGame;
    final AuthService serviceAuth;
    private final Gson gson = new Gson();

    public GameDeleteHandler(GameService serviceGame, AuthService serviceAuth) {
        this.serviceGame = serviceGame;
        this.serviceAuth = serviceAuth;
    }

    @Override
    public void handle(@NotNull Context context) {
        try {
            String authToken = context.header("Authorization");
            AuthResult authResult = serviceAuth.auth(new AuthRequest(authToken));
            if (authResult.resultCode() != 200) {
                context.status(authResult.resultCode());
                context.contentType("application/json");
                context.result(gson.toJson(Map.of("message", authResult.message())));
                return;
            }

            DeleteRequest request = gson.fromJson(context.body(), DeleteRequest.class);
            if (request == null) {
                context.status(400);
                context.contentType("application/json");
                context.result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            LogoutResult result = serviceGame.deleteGame(authToken, request);
            context.status(result.resultCode());
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", result.message())));

        } catch (DataAccessException e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
