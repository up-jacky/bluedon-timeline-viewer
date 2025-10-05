package application;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.awt.Desktop;

import java.net.HttpURLConnection;
import java.net.http.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oauthServices.*;
import oauthServices.DPoPUtil.JWKGenerator;
import viewer.HomePage;
import viewer.LoginPage;
import functionalities.*;

//import viewer.OAuthWebViewPage;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.MessageDigest;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;



public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
        this.primaryStage = stage;
        showLoginPage("INVALID");
        stage.show();
    } catch (Exception e) {
        e.printStackTrace(); // prints the real cause
    }
}
    public static void saveSession(Main.BlueskyOAuth.AuthSession session) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(session);
        Files.write(Paths.get("session.json"), json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static Main.BlueskyOAuth.AuthSession loadSession() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = Files.readAllBytes(Paths.get("session.json"));
        return mapper.readValue(bytes, Main.BlueskyOAuth.AuthSession.class);
    }
    public void showLoginPage(String username) {
        LoginPage loginPage = new LoginPage(this);
        Scene loginScene = new Scene(loginPage.getView(), 1000, 600);
        loginScene.getStylesheets().add("file:resources/styles.css");
        primaryStage.setScene(loginScene);
}

    public void showHomePage(String username) {
        HomePage homePage = new HomePage(this, username);
            // Load session and DPoP keypair
    try {
        BlueskyOAuth.AuthSession session = loadSession();
        // Reconstruct DPoP keypair (same as in your OAuth flow)
        com.nimbusds.jose.jwk.ECKey ecJWK = new com.nimbusds.jose.jwk.ECKey.Builder(
            com.nimbusds.jose.jwk.Curve.P_256,
            new com.nimbusds.jose.util.Base64URL("-lL1dxMP6kqMKgAD_FmJMEgPTxDBivLPM2bM-kLvt2A"),
            new com.nimbusds.jose.util.Base64URL("BQh63v8bDWu9jz0tXFye6ukzGG9hCiGsWOoqNIdUvew")
        )
        .d(new com.nimbusds.jose.util.Base64URL("mdeCxsSPc7ZV1_9vPLOjbj44G3cRtFeIFOaExmxQUPg"))
        .build();
        KeyPair dpopKeyPair = new KeyPair(ecJWK.toECPublicKey(), ecJWK.toECPrivateKey());

        // Fetch the feed
        List<Map<String, Object>> feed = functionalities.BlueskyGetFeed.fetchBlueskyFeed(session, dpopKeyPair);
        //System.out.println("Feed " + feed);
        // Add posts to HomePage
        for (Map<String, Object> post : feed) {
            Map<String, Object> postObj = (Map<String, Object>) post.get("post");
            // Get author info
            Map<String, Object> author = (Map<String, Object>) postObj.get("author");
            String user = (String) author.get("handle");
            
            // Get the record object which contains text and createdAt
            Map<String, Object> record = (Map<String, Object>) postObj.get("record");
            String message = (String) record.get("text");
            String timestamp = (String) record.get("createdAt");
            
            // Add to HomePage
            homePage.addBlueskyPost(user, message, timestamp);
        }
        homePage.refreshPosts();
    } catch (Exception e) {
        e.printStackTrace();
    }

    Scene homeScene = homePage.getView();
    primaryStage.setScene(homeScene);
    primaryStage.setTitle("Bluedon Timeline - Home");
    }

    public static void main(String[] args) {
        launch(args);
    }
    public static class BlueskyOAuth {
        private final Main app;

    public BlueskyOAuth(Main app) {
        this.app = app;
    }

    // Replace with your actual client metadata URL
    private static final String CLIENT_ID = "https://up-jacky.github.io/bluedon-timeline-viewer/oauth/client_metadata.json";
    private static final String REDIRECT_URI = "http://127.0.0.1:8080/callback";
    private static final String AUTH_URL = "https://bsky.social/oauth/authorize";
    private static final String TOKEN_URL = "https://bsky.social/oauth/token";
    

    private String codeVerifier;
    private KeyPair dpopKeyPair;
    private String dpopNonce;

    public class CallbackResult {
    private final String code;
    private final String state;
    private final String error;
    private final String errorDescription;

    public CallbackResult(String code, String state, String error, String errorDescription) {
        this.code = code;
        this.state = state;
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public String code() { return code; }
    public String state() { return state; }
}

    // This class holds tokens and DID
    public static class AuthSession {
        public String accessToken;
        public String refreshToken;
        public String did;
        public String dpopNonce;
        public String codeVerifier;

        // Add this no-arg constructor for Jackson
    public AuthSession() {}

    public AuthSession(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }
}

    public AuthSession startOAuth(String pdsOrigin) throws Exception {
    // 1. Discover endpoints
    String metaUrl = pdsOrigin + "/.well-known/oauth-authorization-server";
    String metaBody = HttpUtil.get(metaUrl, Map.of("Accept", "application/json"));
    Map<String, Object> meta = JsonUtil.fromJson(metaBody);

    String parEndpoint = (String) meta.get("pushed_authorization_request_endpoint");
    String authEndpoint = (String) meta.get("authorization_endpoint");
    String tokenEndpoint = (String) meta.get("token_endpoint");

    System.out.println("PAR endpoint: " + parEndpoint);
    System.out.println("Auth endpoint: " + authEndpoint);
    System.out.println("Token endpoint: " + tokenEndpoint);


    // 2. Generate PKCE
    codeVerifier = PkceUtil.generateCodeVerifier();
    String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);

    //Reconstruct from x, y, d
    com.nimbusds.jose.jwk.ECKey ecJWK = new com.nimbusds.jose.jwk.ECKey.Builder(
        Curve.P_256,
        new Base64URL("-lL1dxMP6kqMKgAD_FmJMEgPTxDBivLPM2bM-kLvt2A"),
        new Base64URL("BQh63v8bDWu9jz0tXFye6ukzGG9hCiGsWOoqNIdUvew")
    )
    .d(new Base64URL("mdeCxsSPc7ZV1_9vPLOjbj44G3cRtFeIFOaExmxQUPg")) // 👈 private scalar
    .build();

    KeyPair dpopKeyPair = new KeyPair(ecJWK.toECPublicKey(), ecJWK.toECPrivateKey());

    AuthSession session = new AuthSession(codeVerifier);

    // 4. Build PAR body (include dpop_bound_access_tokens)
    String state = UUID.randomUUID().toString();
    String parBody = String.format(
        "client_id=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s" +
        "&code_challenge=%s&code_challenge_method=S256&dpop_bound_access_tokens=true",
        urlenc(CLIENT_ID),
        urlenc(REDIRECT_URI),
        urlenc("atproto transition:generic transition:chat.bsky"),
        urlenc(state),
        urlenc(codeChallenge)
    );
    System.out.println("PAR Body: " + parBody);
    System.out.println("PAR endpoint: " + parEndpoint);


    // 5. Send PAR request with DPoP
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/x-www-form-urlencoded");
    headers.put("DPoP", DPoPUtil.buildDPoP("POST", parEndpoint, null, dpopKeyPair, null));
    System.out.println("Header: " + headers);
    

    HttpResponse<String> parResponse = HttpUtil.postFormWithResponse(parEndpoint, headers, parBody);
    System.out.println("ParResponse Statuscode: " + parResponse.statusCode());
    System.out.println("ParResponse body: " + parResponse.body());

    // 6. Retry if server requires a DPoP nonce
    if ((parResponse.statusCode() == 401 || parResponse.statusCode() == 400)
        && parResponse.body().contains("\"use_dpop_nonce\"")) {
    String newNonce = extractDpopNonce(parResponse);
    System.out.println("NewNounc" + newNonce);
    System.out.println("Retried 401");
    if (newNonce != null && !newNonce.isEmpty()) {
        System.out.println("Retried 401 with Nonce: " + newNonce);
        session.dpopNonce = newNonce;
        String dpopJwt = DPoPUtil.buildDPoP("POST", parEndpoint, session.dpopNonce, dpopKeyPair, null);
        System.out.println("DPoP JWT with nonce: " + dpopJwt);
        headers.put("DPoP", dpopJwt);
        parResponse = HttpUtil.postFormWithResponse(parEndpoint, headers, parBody);
    }
}
    System.out.println("par response: " + parResponse);
    // 7. Extract request_uri from PAR response
    Map<String, Object> parJson = JsonUtil.fromJson(parResponse.body());
    String requestUri = (String) parJson.get("request_uri");
    if (requestUri == null || requestUri.isEmpty()){
        System.out.println("Retried PAR Status: " + parResponse.statusCode());
        System.out.println("Retried PAR Headers: " + parResponse.headers());
        System.out.println("Retried PAR Body: " + parResponse.body());
        throw new IOException("PAR failed, no request_uri returned");
    }
    // 8. Open browser for user to authorize
    String authUrl = authEndpoint + "?client_id=" + urlenc(CLIENT_ID)
                     + "&request_uri=" + urlenc(requestUri)
                     + "&state=" + urlenc(state);
    
      LocalCallbackServer callbackServer = new LocalCallbackServer();
    callbackServer.start();

    // Open system browser for OAuth
    Desktop.getDesktop().browse(new URI(authUrl));

    // Wait for authorization code
    LocalCallbackServer.CallbackResult cb = callbackServer.awaitAuthorizationCode(180);
    callbackServer.stop();

    if (cb == null) throw new IOException("Timeout waiting for callback");
    if (!state.equals(cb.state)) throw new IOException("State mismatch");
    if (cb.error != null)
        throw new IOException("Authorization error: " + cb.error + " - " + cb.errorDescription);
    if (cb.code == null) throw new IOException("Authorization code is null");
    System.out.println("Callback result: code=" + cb.code + ", state=" + cb.state + ", error=" + cb.error + ", errorDescription=" + cb.errorDescription);

    String tokenBody = String.format(
    "grant_type=authorization_code&code=%s&redirect_uri=%s&code_verifier=%s&client_id=%s",
    urlenc(cb.code), urlenc(REDIRECT_URI), urlenc(codeVerifier), urlenc(CLIENT_ID)
    );
    System.out.println("Token Body" + tokenBody);
    headers.clear();
    headers.put("Content-Type", "application/x-www-form-urlencoded");
    headers.put("DPoP", DPoPUtil.buildDPoP("POST", tokenEndpoint, session.dpopNonce, dpopKeyPair, null));


    HttpResponse<String> tokenResponse = HttpUtil.postFormWithResponse(tokenEndpoint, headers, tokenBody);
    String newNonce = HttpUtil.extractDpopNonce(tokenResponse);
    if (newNonce != null) session.dpopNonce = newNonce;

    Map<String, Object> tokenJson = JsonUtil.fromJson(tokenResponse.body());
    System.out.println("Token response status: " + tokenResponse.statusCode());
    System.out.println("Token response headers: " + tokenResponse.headers());
    System.out.println("Token response body: " + tokenResponse.body());
    if (tokenJson.containsKey("error"))
        throw new IOException("Token Error: " + tokenJson.get("error") + " - " + tokenJson.get("error_description"));

    session.accessToken = (String) tokenJson.get("access_token");
    session.refreshToken = (String) tokenJson.get("refresh_token");
    session.did = extractDidFromToken(session.accessToken);

    // After successful token exchange
    saveSession(session); // Save tokens for next use

    // Redirect to home page (call from your Main instance)
    app.showHomePage(session.did); 

    return session;
}



    private static String urlenc(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "UTF-8");
    }

    private String extractDidFromToken(String token) {
        // Simple base64 decode from JWT payload
        String payload = token.split("\\.")[1];
        byte[] decoded = java.util.Base64.getUrlDecoder().decode(payload);
        String json = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
        Map<String, Object> map;
        try{
            map = JsonUtil.fromJson(json);
        } catch (Exception e){
            e.printStackTrace();
            map = new HashMap<>();
        }
        
        return (String) map.get("sub");
    }

    public static String extractDpopNonce(HttpResponse<?> response) {
    try {
        String headers = response.headers().toString();
        System.out.println("Header " + headers);


        String body = response.body().toString();
        Map<String, Object> json = JsonUtil.fromJson(body);
        System.out.println("NONCE" + json);
        Object nonce = json.get("DPoP_Nonce");
        if (nonce == null) nonce = json.get("nonce");
        if (nonce == null) nonce = response.headers().firstValue("dpop-nonce").orElse(null);
        if (nonce == null) nonce = response.headers().firstValue("DPoP-Nonce").orElse(null);
        System.out.println("NONCE" + nonce);
        return nonce != null ? nonce.toString() : null;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
       

}

}
