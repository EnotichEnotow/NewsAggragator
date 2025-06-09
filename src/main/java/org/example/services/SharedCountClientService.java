// src/main/java/org/example/services/SharedCountClient.java
package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SharedCountClientService {
    private static final String ENDPOINT = "https://api.sharedcount.com/v1.0/";
    private final String apiKey;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public SharedCountClientService(String apiKey) {
        this.apiKey = apiKey;
    }

    public long fetchShareCount(String url) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(url, StandardCharsets.UTF_8);
        URI uri = URI.create(ENDPOINT + "?apikey=" + apiKey + "&url=" + encoded);

        HttpRequest req = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> rsp = http.send(req, HttpResponse.BodyHandlers.ofString());

        JsonNode root = mapper.readTree(rsp.body());
        long total = 0;
        for (String network : List.of("Facebook", "Pinterest", "Reddit", "LinkedIn")) {
            JsonNode n = root.path(network).path("share_count");
            if (n.isNumber()) total += n.asLong();
        }
        return total;
    }
}
