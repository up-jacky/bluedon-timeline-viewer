package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Map;
import java.util.HashMap;
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
import viewer.HomePage;
import viewer.LoginPage;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.MessageDigest;


public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
        this.primaryStage = stage;
        showLoginPage();
        stage.show();
    } catch (Exception e) {
        e.printStackTrace(); // prints the real cause
    }
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

    // 3. Generate DPoP key pair (EC P-256)
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
    kpg.initialize(256);
    KeyPair dpopKeyPair = kpg.generateKeyPair();
    ECPublicKey pubKey = (ECPublicKey)dpopKeyPair.getPublic();
    ECPrivateKey privKey = (ECPrivateKey)dpopKeyPair.getPrivate();
    ECKey privateECKey = new ECKey.Builder(Curve.P_256, pubKey)
                            .privateKey(privKey)
                            .build();
    ECKey publicJWK = privateECKey.toPublicJWK();

    System.out.println("DPoP keypair generated");


    AuthSession session = new AuthSession(codeVerifier);

    // 4. Build PAR body (include dpop_bound_access_tokens)
    String state = UUID.randomUUID().toString();
    String parBody = String.format(
        "client_id=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s" +
        "&code_challenge=%s&code_challenge_method=S256&dpop_bound_access_tokens=true",
        urlenc(CLIENT_ID),
        urlenc(REDIRECT_URI),
        urlenc("atproto"),
        urlenc(state),
        urlenc(codeChallenge)
    );
    System.out.println("PAR Body: " + parBody);
    System.out.println("PAR endpoint: " + parEndpoint);


    // 5. Send PAR request with DPoP
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/x-www-form-urlencoded");
    headers.put("DPoP", DPoPUtil.buildDPoP("POST", parEndpoint, null, publicJWK));
    System.out.println("Header: " + headers);
    

    HttpResponse<String> parResponse = HttpUtil.postFormWithResponse(parEndpoint, headers, parBody);
    System.out.println("ParResponse Statuscode: " + parResponse.statusCode());

    // 6. Retry if server requires a DPoP nonce
    if ((parResponse.statusCode() == 401 || parResponse.statusCode() == 400)
            && parResponse.body().contains("\"use_dpop_nonce\"")) {
        String newNonce = HttpUtil.extractDpopNonce(parResponse);
        System.out.println("Retried 401");
        if (newNonce != null && !newNonce.isEmpty()) {
            System.out.println("Retried 401 with Nonce");
            session.dpopNonce = newNonce;
            headers.put("DPoP", DPoPUtil.buildDPoP("POST", parEndpoint, session.dpopNonce, publicJWK));
            parResponse = HttpUtil.postFormWithResponse(parEndpoint, headers, parBody);
        }
    }
    System.out.println("par response: " + parResponse);
    // 7. Extract request_uri from PAR response
    Map<String, Object> parJson = JsonUtil.fromJson(parResponse.body());
    String requestUri = (String) parJson.get("request_uri");
    if (requestUri == null || requestUri.isEmpty())
        throw new IOException("PAR failed, no request_uri returned");

    // 8. Open browser for user to authorize
    String authUrl = authEndpoint + "?client_id=" + urlenc(CLIENT_ID)
                     + "&request_uri=" + urlenc(requestUri)
                     + "&state=" + urlenc(state);

    LocalCallbackServer callbackServer = new LocalCallbackServer();
    callbackServer.start();
    Desktop.getDesktop().browse(new URI(authUrl));

    // 9. Wait for authorization code
    LocalCallbackServer.CallbackResult cb = callbackServer.awaitAuthorizationCode(180);
    callbackServer.stop();
    if (cb == null) throw new IOException("Timeout waiting for callback");
    if (!state.equals(cb.state)) throw new IOException("State mismatch");
    if (cb.error != null)
        throw new IOException("Authorization error: " + cb.error + " - " + cb.errorDescription);
    if (cb.code == null) throw new IOException("Authorization code is null");

    // 10. Exchange code for DPoP-bound access token
    String tokenBody = String.format(
        "grant_type=authorization_code&code=%s&redirect_uri=%s&code_verifier=%s&client_id=%s",
        urlenc(cb.code), urlenc(REDIRECT_URI), urlenc(codeVerifier), urlenc(CLIENT_ID)
    );

    headers.clear();
    headers.put("Content-Type", "application/x-www-form-urlencoded");
    headers.put("DPoP", DPoPUtil.buildDPoP("POST", parEndpoint, session.dpopNonce, publicJWK));


    HttpResponse<String> tokenResponse = HttpUtil.postFormWithResponse(tokenEndpoint, headers, tokenBody);
    String newNonce = HttpUtil.extractDpopNonce(tokenResponse);
    if (newNonce != null) session.dpopNonce = newNonce;

    Map<String, Object> tokenJson = JsonUtil.fromJson(tokenResponse.body());
    if (tokenJson.containsKey("error"))
        throw new IOException("Token Error: " + tokenJson.get("error") + " - " + tokenJson.get("error_description"));

    session.accessToken = (String) tokenJson.get("access_token");
    session.refreshToken = (String) tokenJson.get("refresh_token");
    session.did = extractDidFromToken(session.accessToken);

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

    
   

}

}
