package com.bluedon.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Handles HTTP methods.
 */
public class Http {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    /**
     * Encodes the given parameter to a proper form for request in type String.
     * @param params Parameter to encode.
     * @return Encoded form in {@code String}.
     */
    public static String formEncode(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> java.net.URLEncoder.encode(e.getKey(), java.nio.charset.StandardCharsets.UTF_8)
                        + "=" + java.net.URLEncoder.encode(e.getValue(), java.nio.charset.StandardCharsets.UTF_8))
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    /**
     * Sends a HTTP POST request and returns its response.
     * @param url URL to send a POST request to.
     * @param headers Header content of the POST request.
     * @param body Body to pass to the POST request.
     * @return Response for the POST request.
     */
    public static HttpResponse<String> postFormWithResponse(String url, Map<String, String> headers, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(body));

            for (Map.Entry<String, String> h : headers.entrySet()) {
                builder.header(h.getKey(), h.getValue());
            }

            return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("HTTP POST failed: " + url, e);
        }
    }

    /**
     * Sends a HTTP GET request and returns its response.
     * @param url URL to send a GET request to.
     * @param headers Header content of the GET request.
     * @return Response for the GET request.
     */
    public static HttpResponse<String> getWithResponse(String url, Map<String, String> headers) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET();

            for (Map.Entry<String, String> h : headers.entrySet()) {
                builder.header(h.getKey(), h.getValue());
            }

            return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("HTTP GET failed: " + url, e);
        }
    }

    /**
     * Sends a HTTP POST request and returns its response.
     * @param url URL to send a POST request to.
     * @param headers Header content of the POST request.
     * @param body Body to pass to the POST request.
     * @return Body of the response for the POST request.
     */
    public static String postForm(String url, Map<String, String> headers, String body) {
        return postFormWithResponse(url, headers, body).body();
    }
    
    /**
     * Sends a HTTP GET request and returns its response.
     * @param url URL to send a GET request to.
     * @param headers Header content of the GET request.
     * @return Body of the response for the GET request.
     */
    public static String get(String url, Map<String, String> headers) {
        return getWithResponse(url, headers).body();
    }
}
