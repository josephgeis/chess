package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ChessHttpClient {

    HttpClient httpClient = HttpClient.newHttpClient();

    String host;
    int port;

    public ChessHttpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    CompletableFuture<HttpResponse<String>> getAuthenticated(String path, String token) throws Exception {
        HttpRequest request = buildAuthenticatedRequest(path, token)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    CompletableFuture<HttpResponse<String>> post(String path, String data) throws Exception {
        String urlString = buildUrlString(path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    CompletableFuture<HttpResponse<String>> postAuthenticated(String path, String data, String token) throws Exception {
        HttpRequest request = buildAuthenticatedRequest(path, token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    CompletableFuture<HttpResponse<String>> putAuthenticated(String path, String data, String token) throws Exception {
        HttpRequest request = buildAuthenticatedRequest(path, token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(data))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    CompletableFuture<HttpResponse<String>> delete(String path) throws Exception {
        String urlString = buildUrlString(path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
                .DELETE()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    CompletableFuture<HttpResponse<String>> deleteAuthenticated(String path, String token) throws Exception {
        HttpRequest request = buildAuthenticatedRequest(path, token)
                .DELETE()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest.Builder buildAuthenticatedRequest(String path, String token) throws Exception {
        String urlString = buildUrlString(path);

        return HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
                .header("Authorization", token);
    }
    private String buildUrlString(String path) {
        return String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
    }
}
