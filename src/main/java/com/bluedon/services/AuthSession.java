package com.bluedon.services;

public class AuthSession {
    public String handle;
    public String displayName;
    public String avatarUri;
    public String profileUrl;
    
    // Bluesky Specific 
    public String accessJwt;
    public String refreshJwt;
    public String did;

    // Mastodon Specific
    public String accessToken;
    public String refreshToken;
    public String clientId;
    public String clientSecret;
    public String instanceUrl;

}
