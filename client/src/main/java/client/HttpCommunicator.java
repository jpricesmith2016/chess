package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class HttpCommunicator {

    private static HttpClient httpClient = HttpClient.newHttpClient();
    private static String serverURL;
    private static String authToken;

    HttpCommunicator (String serverURL, String authToken) {
        HttpCommunicator.serverURL = serverURL;
        HttpCommunicator.authToken = authToken;
    }

    void setAuthToken(String auth) {
        authToken = auth;
    }

    String getAuthToken() {
        return authToken;
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

    HttpResponse<String> clientHttpBuilder(String path, Boolean auth, String method, String body) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s%s", serverURL, path);
        HttpRequest request = clientRequest(urlString, method, auth, body);

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
