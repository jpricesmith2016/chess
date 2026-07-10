package handler;

import Request_Result.AuthRequest;
import Request_Result.AuthResult;
import Request_Result.GameJoinRequest;
import Request_Result.GameJoinResult;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.AuthService;
import service.GameService;

public class GameJoinHandler implements Handler {

    private final GameService service;
    private final AuthService serviceAuth;
    private final Gson gson = new Gson();

    public GameJoinHandler(GameService service, AuthService serviceAuth) {
        this.service = service;
        this.serviceAuth = serviceAuth;
    }

    @Override
    public void handle(Context context) throws Exception {
        String authToken = context.header("Authorization");
        AuthResult authres = serviceAuth.auth(new AuthRequest(authToken));
        if (authres.resultCode() != 200) {
            context.status(authres.resultCode());
            context.json(authres.message());
        }
        GameJoinRequest request = gson.fromJson(context.body(), GameJoinRequest.class);
        GameJoinResult result = service.joinGame(authToken, request);

        context.status(result.resultCode());
        context.json(result.message());
    }
}
