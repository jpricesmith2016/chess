package handler;

import Request_Result.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.AuthService;
import service.GameService;

import java.util.Map;

public class GameListHandler implements Handler {
    private final GameService service;
    private final AuthService serviceAuth;
    private final Gson gson = new Gson();

    public GameListHandler(GameService service, AuthService serviceAuth) {
        this.service = service;
        this.serviceAuth = serviceAuth;
    }

    @Override
    public void handle(Context context) {
        String authToken = context.header("Authorization");
        AuthResult authres = serviceAuth.auth(new AuthRequest(authToken));
        if (authres.resultCode() != 200) {
            context.status(authres.resultCode());
            context.json(gson.toJson(Map.of("message", authres.message())));
            return;
        }
        ListGamesResult result = service.listGames(authToken);

        context.status(result.resultCode());

        if (result.resultCode() != 200) {
            context.json(gson.toJson(Map.of("message", result.message())));
        } else {
            context.json(gson.toJson(Map.of("games", result.games())));
        }
    }
}
