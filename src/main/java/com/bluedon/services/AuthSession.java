package com.bluedon.services;

public class AuthSession {
    public final String codeVerifier;
    public String accessToken;
    public String refreshToken;
    public String handle;
    public String displayName;
    public String avatarUri;
    public String profileUrl;
    public String dpopNonce;
    
    // Bluesky Specific 
    public String accessJwt;
    public String refreshJwt;
    public String did;

    // Mastodon Specific
    public String clientId;
    public String clientSecret;
    public String instanceUrl;

    public AuthSession(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }
}
