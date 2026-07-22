package client;

import com.google.gson.Gson;
import model.GameData;
import requestresult.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Locale;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static String serverURL;
    private static String authToken;
    private static final Gson gson = new Gson();

    public ServerFacade(String serverURL) {
        ServerFacade.serverURL = serverURL;
        authToken = null;
    }

    private HttpRequest clientRequest(String url, String method, Boolean auth, String body) throws Exception {
        if (auth) {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .method(method, clientBodyPublisher(body))
                    .header("Authorization", authToken)
                    .build();
        } else {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .method(method, clientBodyPublisher(body))
                    .build();
        }
    }

    private HttpRequest.BodyPublisher clientBodyPublisher(String body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    public static void setAuthToken(String authToken) {
        ServerFacade.authToken = authToken;
    }

    public HttpResponse<String> clientHttpBuilder(String path, Boolean auth, String method, String body) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s%s", serverURL, path);
        HttpRequest request = clientRequest(urlString, method, auth, body);

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        HttpResponse<String> response = clientHttpBuilder("/user", false, "POST", gson.toJson(request));

        if (response.statusCode() != 200) {
            return new RegisterResult(response.statusCode(), new RegAuthReturn(request.username(), "")
                    , gson.fromJson(response.body(), String.class));
        }
        return new RegisterResult(response.statusCode(), new RegAuthReturn(request.username()
                , gson.fromJson(response.body(), String.class)), null);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        HttpResponse<String> response = clientHttpBuilder("/session", false, "POST", gson.toJson(request));

        if (response.statusCode() != 200) {
            return new LoginResult(response.statusCode(), gson.fromJson(response.body(), String.class), null);
        }
        authToken = gson.fromJson(response.body(), String.class);
        return new LoginResult(response.statusCode(), request.username(), authToken);
    }

    public LogoutResult logout(LogoutRequest request) throws Exception {
        authToken = request.authToken();
        HttpResponse<String> response = clientHttpBuilder("/session", true, "DELETE", null);

        return new LogoutResult(response.statusCode(), gson.fromJson(response.body(), String.class));
    }

    public CreateResult createGame(CreateRequest request) throws Exception {
        HttpResponse<String> response = clientHttpBuilder("/game", true, "POST", gson.toJson(request));

        if (response.statusCode() != 200) {
            return new CreateResult(response.statusCode(),0, gson.fromJson(response.body(), String.class));
        }
        return new CreateResult(response.statusCode(), gson.fromJson(response.body(), Integer.class), "");
    }

    public GameJoinResult joinGame(GameJoinRequest request) throws Exception {
        HttpResponse<String> response = clientHttpBuilder("/game", true, "PUT", gson.toJson(request));

        return new GameJoinResult(response.statusCode(), gson.fromJson(response.body(), String.class));
    }

    public ListGamesResult listGames() throws Exception {
        HttpResponse<String> response = clientHttpBuilder("/game", true, "GET", null);

        if (response.statusCode() != 200) {
            return new ListGamesResult(response.statusCode(),gson.fromJson(response.body(), String.class), null);
        }
        GameData[] games = gson.fromJson(response.body(), GameData[].class);

        return new ListGamesResult(response.statusCode(),"", Arrays.asList(games));
    }

    public void clear() throws Exception {
        clientHttpBuilder("/db", false, "DELETE", null);
    }
}
