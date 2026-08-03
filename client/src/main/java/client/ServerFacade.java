package client;

import com.google.gson.Gson;
import requestresult.*;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;

public class ServerFacade {
    private static String serverURL;
    private static Gson gson = new Gson();
    private static HttpCommunicator clientHttp;

    public ServerFacade(String serverURL) {
        ServerFacade.serverURL = serverURL;
        clientHttp = new HttpCommunicator(serverURL, null);
    }

    public ServerFacade(int port) {
        ServerFacade.serverURL = "http://localhost:" + port;
        clientHttp = new HttpCommunicator(serverURL, null);
    }


    public RegisterResult register(RegisterRequest request) throws Exception {
        HttpResponse<String> response = clientHttp.clientHttpBuilder("/user", false, "POST", gson.toJson(request));

        if (response.statusCode() != 200) {
            MessageResponse responseStr = gson.fromJson(response.body(), (Type) MessageResponse.class);
            return new RegisterResult(response.statusCode(), new RegAuthReturn(request.username(), "")
                    , responseStr.message());
        }
        RegAuthReturn auth = gson.fromJson(response.body(), RegAuthReturn.class);
        clientHttp.setAuthToken(auth.authToken());
        return new RegisterResult(response.statusCode(), auth, null);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        HttpResponse<String> response = clientHttp.clientHttpBuilder("/session", false, "POST", gson.toJson(request));

        if (response.statusCode() != 200) {
            MessageResponse responseStr = gson.fromJson(response.body(), (Type) MessageResponse.class);
            return new LoginResult(response.statusCode(), responseStr.message(), null);
        }
        RegAuthReturn regAuth = gson.fromJson(response.body(), (Type) RegAuthReturn.class);
        clientHttp.setAuthToken(regAuth.authToken());
        return new LoginResult(response.statusCode(), request.username(), clientHttp.getAuthToken());
    }

    public LogoutResult logout(LogoutRequest request) throws Exception {
        clientHttp.setAuthToken(request.authToken());
        HttpResponse<String> response = clientHttp.clientHttpBuilder("/session", true, "DELETE", null);

        MessageResponse responseStr = gson.fromJson(response.body(), (Type) MessageResponse.class);
        clientHttp.setAuthToken(null);
        return new LogoutResult(response.statusCode(), responseStr == null ? "" : responseStr.message());
    }

    public CreateResult createGame(CreateRequest request) throws Exception {
        HttpResponse<String> response = clientHttp.clientHttpBuilder("/game", true, "POST", gson.toJson(request));

        if (response.statusCode() != 200) {
            MessageResponse responseStr = gson.fromJson(response.body(), (Type) MessageResponse.class);
            return new CreateResult(response.statusCode(),0, responseStr.message());
        }
        IdResponse id = gson.fromJson(response.body(), IdResponse.class);
        return new CreateResult(response.statusCode(), id.gameID(), "");
    }

    public LogoutResult deleteGame(DeleteRequest request) throws Exception {
        HttpResponse<String> response = clientHttp.clientHttpBuilder("/game", true, "DELETE", gson.toJson(request));
        MessageResponse responseStr = gson.fromJson(response.body(), (Type) MessageResponse.class);
        return new LogoutResult(response.statusCode(), responseStr.message());
    }

    public GameJoinResult joinGame(GameJoinRequest request) throws Exception {
        HttpResponse<String> response = clientHttp.clientHttpBuilder("/game", true, "PUT", gson.toJson(request));

        if (response.statusCode() != 200) {
            MessageResponse responseStr = gson.fromJson(response.body(), (Type) MessageResponse.class);
            return new GameJoinResult(response.statusCode(), responseStr.message());
        }
        return new GameJoinResult(response.statusCode(), "");
    }

    public ListGamesResult listGames() throws Exception {
        HttpResponse<String> response = clientHttp.clientHttpBuilder("/game", true, "GET", null);

        if (response.statusCode() != 200) {
            MessageResponse messageResponse = gson.fromJson(response.body(), MessageResponse.class);
            return new ListGamesResult(response.statusCode(), messageResponse.message(), null);
        }
        ListGamesResponse games = gson.fromJson(response.body(), (Type) ListGamesResponse.class);

        return new ListGamesResult(response.statusCode(),"", games.games());
    }

    public void clear() throws Exception {
        clientHttp.clientHttpBuilder("/db", false, "DELETE", null);
    }
}
