package handler;

import dataaccess.exceptions.DataAccessException;
import requestresult.AuthRequest;
import requestresult.AuthResult;
import requestresult.GameJoinRequest;
import requestresult.GameJoinResult;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.AuthService;
import service.GameService;

import java.util.Map;

public class GameJoinHandler implements Handler {

    private final GameService service;
    private final AuthService serviceAuth;
    private final Gson gson = new Gson();

    public GameJoinHandler(GameService service, AuthService serviceAuth) {
        this.service = service;
        this.serviceAuth = serviceAuth;
    }

    @Override
    public void handle(Context context) {
        try {
            String authToken = context.header("Authorization");
            AuthResult authres = serviceAuth.auth(new AuthRequest(authToken));
            if (authres.resultCode() != 200) {
                context.status(authres.resultCode());
                context.json(gson.toJson(Map.of("message", authres.message())));
                return;
            }

            GameJoinRequest request = gson.fromJson(context.body(), GameJoinRequest.class);

            GameJoinResult result = service.joinGame(authToken, request);
            context.status(result.resultCode());

            if (result.resultCode() != 200) {
                context.json(gson.toJson(Map.of("message", result.message())));
            }
        } catch (DataAccessException e) {
            context.status(500);
            context.json(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
