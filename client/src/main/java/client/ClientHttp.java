package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class ClientHttp {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static String serverURL;
    private static String authToken;

    public ClientHttp(String serverURL) {
        ClientHttp.serverURL = serverURL;
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
        ClientHttp.authToken = authToken;
    }

    public HttpResponse<String> clientHttpBuilder(String path, Boolean auth, String method, String body) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s%s", serverURL, path);
        HttpRequest request = clientRequest(urlString, method, auth, body);

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            System.out.println(httpResponse.body());
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
        }

        return httpResponse;
    }
}
