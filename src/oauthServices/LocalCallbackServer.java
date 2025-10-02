package oauthServices;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LocalCallbackServer {

    private HttpServer server;
    private final CompletableFuture<CallbackResult> callbackFuture = new CompletableFuture<>();

    public LocalCallbackServer() { }

    // Start the local server on 127.0.0.1:8080
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);

        server.createContext("/callback", this::handleCallback);

        server.start();
        System.out.println("Local callback server started at http://127.0.0.1:8080/callback");
    }

    private void handleCallback(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryParams(query);

            String code = params.get("code");
            String state = params.get("state");
            String iss = params.get("iss");
            String error = params.get("error");
            String errorDescription = params.get("error_description");


            // Complete the future to return to the caller
            callbackFuture.complete(new CallbackResult(code, state, iss, error, errorDescription));


            // Respond to the browser
            String html = "<html><body><h1>Login successful!</h1><p>You can close this window.</p></body></html>";
            byte[] responseBytes = html.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
                os.flush();
            }

            stop(); // stop the server after handling one callback
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    // Stop the server
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Local callback server stopped.");
        }
    }

    // Wait for the authorization code (with timeout)
    public CallbackResult awaitAuthorizationCode(int timeoutSeconds) {
        try {
            return callbackFuture.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    // Parse query parameters from URL
    private Map<String, String> parseQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(
                        pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        pair -> pair.length > 1 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : ""
                ));
    }

    // Result holder
    public static class CallbackResult {
    public final String code;
    public final String state;
    public final String iss;
    public final String error;             // add this
    public final String errorDescription;  // add this

    public CallbackResult(String code, String state, String iss, String error, String errorDescription) {
        this.code = code;
        this.state = state;
        this.iss = iss;
        this.error = error;
        this.errorDescription = errorDescription;
    }
}


  

 {
    
}
}
