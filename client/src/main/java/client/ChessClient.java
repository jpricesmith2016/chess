package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class ChessClient {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static String serverURL;
    private static String authToken;

    public ChessClient(String serverURL) throws Exception {
        ChessClient.serverURL = serverURL;
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

    private HttpRequest.BodyPublisher clientBodyPublisher(String body) throws IOException {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private void get(String serverURL, String path, Boolean auth) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s%s", serverURL, path);
        HttpRequest request = clientRequest(urlString, "get", auth, null);

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            System.out.println(httpResponse.body());
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
        }
    }

    public static Boolean preAuthRun() {
        return false;
    }

    public static Boolean postAuthRun() {
        return false;
    }
}
