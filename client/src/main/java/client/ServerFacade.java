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

import model.*;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static String serverURL;
    private static String authToken;
    private static Gson gson = new Gson();

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

    }

    public LoginResult login(LoginRequest request) throws Exception {

    }

    public LogoutResult logout(LogoutRequest request) throws Exception {

    }

    public AuthResult auth(AuthRequest request) throws Exception {

    }

    public GameJoinResult joinGame(GameJoinRequest request) throws Exception {

    }

    public ListGamesResult listGames() throws Exception {
        HttpResponse<String> response = clientHttpBuilder("/game", false, "GET", authToken);

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
