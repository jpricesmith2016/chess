package handler;

import Request_Result.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.AuthService;

import java.util.Map;

public class GameCreateHandler implements Handler {
    final GameService serviceGame;
    final AuthService serviceAuth;
    private final Gson gson = new Gson();

    public GameCreateHandler(GameService serviceGame, AuthService serviceAuth) {
        this.serviceGame = serviceGame;
        this.serviceAuth = serviceAuth;
    }

    @Override
    public void handle(@NotNull Context context) {
        String authToken = context.header("Authorization");
        AuthResult authres = serviceAuth.auth(new AuthRequest(authToken));
        if (authres.resultCode() != 200) {
            context.status(authres.resultCode());
            context.json(gson.toJson(Map.of("message", authres.message())));
            return;
        }

        CreateRequest request = gson.fromJson(context.body(), CreateRequest.class);
        if (request == null) {
            context.status(400);
            context.json(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }
        CreateResult result = serviceGame.createGame(authToken, request);

        context.status(result.resultCode());

        if (result.resultCode() != 200) {
            context.json(gson.toJson(Map.of("message", result.message())));
        } else {
            context.json(gson.toJson(Map.of("gameID", result.gameID())));
        }
    }
}
