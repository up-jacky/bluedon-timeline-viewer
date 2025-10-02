package oauthServices;

import java.net.http.*;
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
        return null;
    }
}
