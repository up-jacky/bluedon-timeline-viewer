package com.bluedon.services;

/**
 * Contains information about the current session of a service.
 */
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
