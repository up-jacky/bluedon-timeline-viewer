package oauthServices;

import java.net.http.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class HttpUtil {
    public static String get(String url, Map<String, String> headers) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).GET();
        if (headers != null) headers.forEach(builder::header);
        HttpResponse<String> resp = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    public static HttpResponse<String> postFormWithResponse(String url, Map<String, String> headers, String body) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (headers != null) headers.forEach(builder::header);
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static String extractDpopNonce(HttpResponse<String> resp) {
        // stub
        return resp.headers()
        .firstValue("DPoP-Nonce")
        .or(() -> resp.headers().firstValue("dpop-nonce"))
        .orElse(null);
    }
    public static HttpResponse<String> getWithResponse(String url, Map<String, String> headers) throws IOException, InterruptedException {
    HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET();
    for (Map.Entry<String, String> entry : headers.entrySet()) {
        builder.header(entry.getKey(), entry.getValue());
    }
    HttpClient client = HttpClient.newHttpClient();
    return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
