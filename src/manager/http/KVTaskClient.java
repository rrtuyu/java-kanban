package manager.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private static final String REG_ENDPOINT = "/register";
    private static final String SAVE_ENDPOINT = "/save/";
    private static final String LOAD_ENDPOINT = "/load/";

    private String apiToken;
    private URI host;
    private HttpClient client;
    private HttpResponse.BodyHandler<String> rHandler;

    public KVTaskClient(String url) throws KVClientException {
        this.client = HttpClient.newHttpClient();
        this.host = URI.create(url);
        this.rHandler = HttpResponse.BodyHandlers.ofString();
        this.apiToken = assignToken();
    }

    public void put(String key, String json) {
        URI uri = host.resolve(SAVE_ENDPOINT + key + "?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest req = HttpRequest.newBuilder()
                .POST(publisher)
                .uri(uri)
                .build();

        try {
            HttpResponse<String> resp = client.send(req, rHandler);
            int statusCode = resp.statusCode();
            if (statusCode != 200)
                throw new IOException(String.format("Failed put an element\n%s: %s\nServer response code: %d", key, json, statusCode));
        } catch (IOException | InterruptedException e) {
            throw new KVClientException(e.getMessage());
        }
    }

    public String load(String key) {
        URI uri = host.resolve(LOAD_ENDPOINT + key + "?API_TOKEN=" + apiToken);

        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        try {
            HttpResponse<String> resp = client.send(req, rHandler);
            if (resp.statusCode() != 200)
                throw new KVClientException(String.format("Couldn't load an element\nKEY: %s", key));
            return resp.body();
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Load error occurred, check request body/server status");
        }
    }

    private String assignToken() throws KVClientException {
        URI regUri = host.resolve(REG_ENDPOINT);
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(regUri)
                .build();

        try {
            HttpResponse<String> resp = client.send(req, rHandler);
            if (resp.statusCode() != 200)
                throw new KVClientException("Can't register" + resp.statusCode());
            return resp.body();
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Unable to get API token");
        }
    }
}
