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

    void get(String path) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
                .GET()
                .build();

//        HttpResponse<String> response =
//                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
