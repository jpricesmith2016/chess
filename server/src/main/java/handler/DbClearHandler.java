package handler;

import dataaccess.exceptions.DataAccessException;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class DbClearHandler implements Handler {
    private final GameService serviceGame;
    private final AuthService serviceAuth;
    private final UserService serviceUser;
    private final Gson gson = new Gson();

    public DbClearHandler(GameService gameSer, AuthService authSer, UserService userSer) {
        serviceGame = gameSer;
        serviceAuth = authSer;
        serviceUser = userSer;
    }

    @Override
    public void handle(Context context) {
        try {
        serviceGame.clear();
        serviceAuth.clear();
        serviceUser.clear();

        context.status(200);
        } catch (DataAccessException e) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
