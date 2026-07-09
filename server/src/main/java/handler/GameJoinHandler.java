package handler;

import Request_Result.GameJoinRequest;
import Request_Result.GameJoinResult;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.GameService;

public class GameJoinHandler implements Handler {

    private final GameService service;
    private final Gson gson = new Gson();

    public GameJoinHandler(GameService service) {
        this.service = service;
    }

    @Override
    public void handle(Context context) throws Exception {
        String authToken = context.header("Authorization");
        GameJoinRequest request = gson.fromJson(context.body(), gameJoinRequest.class);
        GameJoinResult result = service.joinGame(authToken, request);

        context.status(200);
        context.json(result);
    }
}
