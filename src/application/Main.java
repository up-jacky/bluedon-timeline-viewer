package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.http.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import viewer.HomePage;
import viewer.LoginPage;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;


public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        showLoginPage();
        stage.show();
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        Scene loginScene = new Scene(loginPage.getView(), 1000, 600);
        loginScene.getStylesheets().add("file:resources/styles.css");
        primaryStage.setScene(loginScene);
    }

    public void showHomePage(String username) {
        HomePage homePage = new HomePage(this, username);
        Scene homeScene = homePage.getView();  // already a Scene
        primaryStage.setScene(homeScene);
        primaryStage.setTitle("Bluedon Timeline - Home");
    }
    
    /* public void showHomePage(String username) {
        HomePage homePage = new HomePage(this, username);
        Scene homeScene = new Scene(homePage.getView(),1000, 600);
        primaryStage.setTitle("Bluedon Timeline Viewer - Home");
        primaryStage.setScene(homeScene);
    } */

    public static void main(String[] args) {
        launch(args);
    }




    public static class BlueskyOAuth {

    private static final String CLIENT_ID = "https://up-jacky.github.io/bluedon-timeline-viewer/oauth/client_metadata.json";
    private static final String REDIRECT_URI = "http://127.0.0.1:8080/callback";
    private static final String AUTH_URL = "https://bsky.social/oauth/authorize";
    private static final String TOKEN_URL = "https://bsky.social/oauth/token";
    private static final String FEED_URL = "https://bsky.social/xrpc/app.bsky.feed.getAuthorFeed";


    private String codeVerifier;

    public void startOAuth() {
        Stage stage = new Stage();
        VBox root = new VBox();
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // PKCE
        codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        // Build Auth URL
        String authUrl = AUTH_URL
                + "?response_type=code"
                + "&client_id=" + url(CLIENT_ID)
                + "&redirect_uri=" + url(REDIRECT_URI)
                + "&scope=" + url("atproto")
                + "&code_challenge=" + url(codeChallenge)
                + "&code_challenge_method=S256";

        engine.load(authUrl);

        // Listen for redirect
        engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            if (newLoc.startsWith(REDIRECT_URI)) {
                if (newLoc.contains("code=")) {
                    String code = newLoc.substring(newLoc.indexOf("code=") + 5);
                    if (code.contains("&")) {
                        code = code.substring(0, code.indexOf("&"));
                    }
                    System.out.println("Authorization Code: " + code);

                    // Exchange code for tokens
                    try {
                        exchangeCodeForToken(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stage.close();
                }
            }
        });

        root.getChildren().add(webView);
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Login with Bluesky");
        stage.show();
    }

    private void exchangeCodeForToken(String code) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String body = "grant_type=authorization_code"
                + "&client_id=" + url(CLIENT_ID)
                + "&redirect_uri=" + url(REDIRECT_URI)
                + "&code=" + url(code)
                + "&code_verifier=" + url(codeVerifier);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println("Token response: " + responseBody);

        String accessToken = "";
        String did = "";

        if (responseBody.contains("\"access_token\"")) {
            accessToken = responseBody.split("\"access_token\":\"")[1].split("\"")[0];
        }
        if (responseBody.contains("\"sub\"")) {
            did = responseBody.split("\"sub\":\"")[1].split("\"")[0];
        }

        System.out.println("Access Token: " + accessToken);
        System.out.println("DID: " + did);

        fetchFeed(accessToken, did);
    }


    private void fetchFeed(String token, String did) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url = FEED_URL + "?actor=" + url(did);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Feed response: " + response.body());
    }

    // Utils
    private static String generateCodeVerifier() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private static String generateCodeChallenge(String verifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String url(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}

}
