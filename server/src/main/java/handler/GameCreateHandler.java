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
    GameService serviceGame;
    AuthService serviceAuth;
    private final Gson gson = new Gson();

    public GameCreateHandler(GameService serviceGame, AuthService serviceAuth) {
        this.serviceGame = serviceGame;
        this.serviceAuth = serviceAuth;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String authToken = context.header("Authorization");
        AuthResult authres = serviceAuth.auth(new AuthRequest(authToken));
        if (authres.resultCode() != 200) {
            context.status(authres.resultCode());
            context.json(Map.of("message", authres.message()));
            return;
        }

        CreateRequest request = gson.fromJson(context.body(), CreateRequest.class);;
        CreateResult result = serviceGame.createGame(authToken, request);

        String jsonResult = gson.toJson(Map.of("message", result.message()));

        if (result.resultCode() != 200) {
            context.contentType("application/json");
            context.json(jsonResult);
        } else {
            jsonResult = gson.toJson(Map.of("gameID", result.gameID()));
            context.json(jsonResult);
        }
    }
}
