package handler;

import io.javalin.http.Handler;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.AuthService;
import service.GameService;
import service.UserService;

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
        serviceGame.clear();
        serviceAuth.clear();
        serviceUser.clear();

        context.status(200);
    }
}
