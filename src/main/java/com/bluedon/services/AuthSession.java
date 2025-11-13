package com.bluedon.services;

/**
 * Contains information about the current session of a service.
 */
public class AuthSession {
    /**
     * Handle of the user.
     */
    public String handle;

    /**
     * Display name of the user.
     */
    public String displayName;

    /**
     * Link to the avatar of the user.
     */
    public String avatarUri;

    /**
     * Link to the profile of the user.
     */
    public String profileUrl;
    
    // Bluesky Specific 
    /**
     * Used to gain access for authenticated endpoints.
     */
    public String accessJwt;

    /**
     * Used to refresh and delete the current active session.
     */
    public String refreshJwt;

    /**
     * Unique identfier for Bluesky.
     */
    public String did;

    // Mastodon Specific

    /**
     * Used to gain access for authorized endpoints.
     */
    public String accessToken;

    /**
     * Not used.
     */
    public String refreshToken;

    /**
     * Used to revoke the Auth for Mastodon.
     */
    public String clientId;

    /**
     * Used to revoke the Auth for Mastodon.
     */
    public String clientSecret;

    /**
     * Starting URL for accessing the API in Mastodon.
     */
    public String instanceUrl;

}
