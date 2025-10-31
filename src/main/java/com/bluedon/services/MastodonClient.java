package com.bluedon.services;

import com.bluedon.utils.Http;
import com.bluedon.utils.LocalCallbackServer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;

public class MastodonClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private static final String CLIENT_NAME = "bluedon";
    private static final String REDIRECT_URI = "http://127.0.0.1:8080/callback";
    private static final String SCOPES = "read:statuses profile";

    private final Map<String, InstanceCredentials> instanceCredentials = new HashMap<>();

    private static class InstanceCredentials {
        final String clientId;
        final String clientSecret;

        InstanceCredentials(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }
    }

    public InstanceCredentials registerApp(String instanceUrl) throws Exception {
        String appsEndpoint = instanceUrl + "/api/v1/apps";

        Map<String, String> appParams = Map.of(
                "client_name", CLIENT_NAME,
                "redirect_uris", REDIRECT_URI,
                "scopes", SCOPES
        );

        String requestBody = Http.formEncode(appParams);
        Map<String, String> headers = Map.of("Content-Type", "application/x-www-form-urlencoded");

        var response = Http.postFormWithResponse(appsEndpoint, headers, requestBody);

        if (response.statusCode() != 200) {
            throw new IOException("App registration failed with status: " + response.statusCode() + ", body: " + response.body());
        }

        var appData = MAPPER.readValue(response.body(), Map.class);
        String clientId = (String) appData.get("client_id");
        String clientSecret = (String) appData.get("client_secret");

        if (clientId == null || clientSecret == null) {
            throw new IOException("App registration response missing client_id or client_secret: " + response.body());
        }

        InstanceCredentials creds = new InstanceCredentials(clientId, clientSecret);
        instanceCredentials.put(instanceUrl, creds);
        System.out.println("Registered app on " + instanceUrl + ", Client ID: " + clientId);
        return creds;
    }

    /**
     * Starts the OAuth flow using a user-provided handle or instance URL.
     * Resolves the handle/URL to the instance URL, registers the app if necessary,
     * initiates the browser flow, handles the callback, and exchanges the code for a token.
     * @return An AuthSession object containing the access token and resolved instance URL.
     * @throws Exception If the flow fails.
     */
    public AuthSession startAuth() throws Exception {
        String instanceUrl = "https://mastodon.social";

        // Check if credentials are available, register if not
        InstanceCredentials creds = instanceCredentials.get(instanceUrl);
        if (creds == null) {
            creds = registerApp(instanceUrl);
        }

        AuthSession session = new AuthSession(null);
        String authEndpoint = instanceUrl + "/oauth/authorize";

        String state = UUID.randomUUID().toString();
        String authUrl = authEndpoint +
                "?client_id=" + urlenc(creds.clientId) +
                "&redirect_uri=" + urlenc(REDIRECT_URI) +
                "&response_type=code" +
                "&scope=" + urlenc(SCOPES) +
                "&state=" + urlenc(state);

        LocalCallbackServer callbackServer = new LocalCallbackServer();
        try {
            callbackServer.start();
            waitForLocalServer("127.0.0.1", 8080, 2000);
            
            // Open system browser. If Desktop fails, print URL so caller can open manually.
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create(authUrl));
                } else {
                    System.out.println("Open this URL in your browser: " + authUrl);
                }
            } catch (Exception e) {
                System.out.println("Failed to open system browser: " + e.getMessage());
                System.out.println("Open this URL in your browser: " + authUrl);
            }

            // Wait for callback
            LocalCallbackServer.CallbackResult cb = callbackServer.awaitAuthorizationCode(180);
            if (cb == null) throw new IOException("Timeout waiting for callback");
            if (!state.equals(cb.state())) throw new IOException("State mismatch");

            // Token exchange
            String tokenEndpoint = instanceUrl + "/oauth/token";
            String tokenBody = String.format(
                "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                urlenc(creds.clientId), urlenc(creds.clientSecret), urlenc(cb.code()), urlenc(REDIRECT_URI)
            );

            Map<String, String> tokenHeaders = Map.of(
                    "Content-Type", "application/x-www-form-urlencoded"
            );

            var tokenResponse = Http.postFormWithResponse(tokenEndpoint, tokenHeaders, tokenBody);

            if (tokenResponse.statusCode() != 200) {
                throw new IOException("Token exchange failed with status: " + tokenResponse.statusCode() + ", body: " + tokenResponse.body());
            }

            System.out.println("[INFO] Auth Headers: " + tokenResponse.headers());

            var tokenData = MAPPER.readValue(tokenResponse.body(), Map.class);

            String accessToken = (String) tokenData.get("access_token");
            String refreshToken = (String) tokenData.get("refresh_token");
            String tokenType = (String) tokenData.get("token_type");

            // Check for errors
            if (accessToken == null) {
                throw new IOException("Token exchange response missing access_token: " + tokenResponse.body());
            }
            if (!"Bearer".equalsIgnoreCase(tokenType)) {
                System.out.println("Warning: Expected token_type 'Bearer', got '" + tokenType + "'");
            }

            // Save client_id and client_secret for revoking the OAuth
            session.clientId = creds.clientId;
            session.clientSecret = creds.clientSecret;

            // Supply session tokens
            session.accessToken = accessToken;
            session.refreshToken = refreshToken;
            session.instanceUrl = instanceUrl;

            return session;

        } finally {
             try {
                callbackServer.stop();
            } catch (Exception ignored) {}
        }
    }

    private static void waitForLocalServer(String host, int port, int maxMillis) {
        long deadline = System.currentTimeMillis() + maxMillis;
        while (System.currentTimeMillis() < deadline) {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(host, port), 250);
                return;
            } catch (IOException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void revokeAuth(AuthSession session) throws Exception {
        if (session.accessToken == null || session.instanceUrl == null) {
            throw new IllegalStateException("Session is not authenticated or missing instance URL.");
        }

        String postEndpoint = session.instanceUrl + "/oauth/revoke";

        String body = String.format("client_id=%s&client_secret=%s&token=%s", 
            urlenc(session.clientId),  urlenc(session.clientSecret), urlenc(session.accessToken));

        Map<String, String> headers = Map.of(
            "Content-Type", "application/x-www-form-urlencoded"
        );

        var response = Http.postFormWithResponse(postEndpoint, headers, body);

        if (response.statusCode() != 200) {
            throw new IOException("Revoke token failed with status: " + response.statusCode() + ", body: " + response.body());
        } else {
            System.out.println("[INFO] Succesfully revoked the current session.");
        }
    }

    public void getUserInfo(AuthSession session) throws Exception {
        if (session.accessToken == null || session.instanceUrl == null) {
            throw new IllegalStateException("Session is not authenticated or missing instance URL.");
        }

        String postEndpoint = session.instanceUrl + "/oauth/userinfo";

        Map<String, String> headers = Map.of(
            "Authorization", "Bearer " + session.accessToken
        );

        var response = Http.getWithResponse(postEndpoint, headers);
        

        if (response.statusCode() != 200) {
            throw new IOException("Get user info failed with status: " + response.statusCode() + ", body: " + response.body());
        } else {
            System.out.println("[INFO] Succesfully get user info.");
            JSONObject responseBody = new JSONObject(response.body());
            session.handle = responseBody.getString("preferred_username") + "@mastodon.social";
            session.displayName = responseBody.getString("name").isEmpty()? responseBody.getString("preferred_username"): responseBody.getString("name");
            session.avatarUri = responseBody.getString("picture");
            session.profileUrl = responseBody.getString("profile");
        }

    }

    public JSONObject getTimeline(AuthSession session) throws Exception {
        if (session.accessToken == null || session.instanceUrl == null) {
            System.out.println("[ERROR] access_token=\""+ session.accessToken + "\"" + "instance_url=\"" + session.instanceUrl + "\"");
            throw new IllegalStateException("Session is not authenticated or missing instance URL.");
        }

        String postEndpoint = session.instanceUrl + "/api/v1/timelines/home";

        Map<String, String> headers = Map.of(
            "Authorization", "Bearer " + session.accessToken
        );

        var response = Http.getWithResponse(postEndpoint, headers);
        JSONObject json = new JSONObject("{\"feed\": " + response.body() + "}");

        if (response.statusCode() != 200) {
            throw new IOException("View timeline failed with status: " + response.statusCode() + ", body: " + response.body());
        }

        return json;
    }

    private static String urlenc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
