package com.bluedon.services;

/**
 * The ServiceRegistry contains the current session of both the Bluesky and Mastodon.
 */
public class ServiceRegistry {
    private static BlueskyClient blueskyClient = new BlueskyClient();
    private static AuthSession blueskySession;
    private static String blueskyPdsOrigin = "https://bsky.social";

    private static MastodonClient mastodonClient = new MastodonClient();
    private static AuthSession mastodonSession;

    /**
     * @return {@link BlueskyClient}
     */
    public static BlueskyClient getBlueskyClient() {
        return blueskyClient;
    }

    /**
     * @return Current {@link AuthSession} of Bluesky
     */
    public static AuthSession getBlueskySession() {
        return blueskySession;
    }

    /**
     * Sets the session of the Bluesky to the provided parameter session.
     * @param session
     */
    public static void setBlueskySession(AuthSession session) {
        blueskySession = session;
    }

    /**
     * @return {@code "https://bsky.social}" in {@code String}
     */
    public static String getBlueskyPdsOrigin() {
        return blueskyPdsOrigin;
    }
    
    /**
     * @return {@code true} If there is a current Bluesky session and its accessJwt is not empty.
     */
    public static boolean isBlueskyLoggedIn() {
        return (blueskySession != null && blueskySession.accessJwt != null);
    }

    /**
     * @return {@link BlueskyClient}
     */
    public static MastodonClient getMastodonClient() {
        return mastodonClient;
    }

    /**
     * @return Current {@link AuthSession} of Mastodon
     */
    public static AuthSession getMastodonSession() {
        return mastodonSession;
    }
    
    /**
     * Sets the session of the Mastodon to the provided parameter session.
     * @param session
     */
    public static void setMastodonSession(AuthSession session) {
        mastodonSession = session;
    }

    /**
     * @return {@code true} If there is a current Mastodon session.
     */
    public static boolean isMastodonLoggedIn() {
        return (mastodonSession != null);
    }
}