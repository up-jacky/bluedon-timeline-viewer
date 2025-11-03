package com.bluedon.utils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import com.bluedon.services.AuthSession;
import com.bluedon.services.BlueskyClient;
import com.bluedon.services.MastodonClient;
import com.bluedon.services.ServiceRegistry;

public class SessionFile {
    public static void readSession() {
        BlueskySessionFile.readSession();
        MastodonSessionFile.readSession();
    }

    public class BlueskySessionFile {
        private static String fileName = "bluesky-session.json";
        public static void saveSession() {
            AuthSession session = ServiceRegistry.getBlueskySession();
            try (FileWriter writer = new FileWriter(fileName)) {
                String codeVerifier = session.codeVerifier == null? "" : session.codeVerifier;
                String accessToken = session.accessToken == null? "" : session.accessToken;
                String refreshToken = session.refreshToken == null? "" : session.refreshToken;
                String accessJwt = session.accessJwt == null? "" : session.accessJwt;
                String refreshJwt = session.refreshJwt == null? "" : session.refreshJwt;
                String did = session.did == null? "" : session.did;
                String dpopNonce = session.dpopNonce == null? "" : session.dpopNonce;
                String data = String.format("""
{
    "code_verifier": "%s",
    "access_token": "%s",
    "refresh_token": "%s",
    "did": "%s",
    "dpop_nonce": "%s",
    "access_jwt": "%s",
    "refresh_jwt": "%s",
}
            """, codeVerifier, accessToken, refreshToken, did, dpopNonce, accessJwt, refreshJwt);
                writer.write(data);
                System.out.println("[INFO][SessionFile][BlueskySessionFile][saveSession] Successfully saved Bluesky session.");
            } catch (Exception e) {
                System.err.println("[ERROR][SessionFile][BlueskySessionFile][saveSession] Failed to save current Bluesky session. " + e.getMessage());
                e.printStackTrace();
            }
        }

        public static void readSession() {
            try {
                String pathString = System.getProperty("user.dir") + "\\" + fileName;
                Path filePath = Paths.get(pathString);
                String jsonString = String.join("",Files.readAllLines(filePath));
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.getString("code_verifier").isEmpty()) {
                    System.out.println("[INFO][SessionFile][BlueskySessionFile][readSession] No saved Bluesky session.");
                    return;
                }
                AuthSession session = new AuthSession(jsonObject.getString("code_verifier"));
                session.accessToken = jsonObject.getString("access_token");
                session.refreshToken = jsonObject.getString("refresh_token");
                session.did = jsonObject.getString("did");
                session.dpopNonce = jsonObject.getString("dpop_nonce");
                session.accessJwt = jsonObject.getString("access_jwt").isEmpty()? null : jsonObject.getString("access_jwt");
                session.refreshJwt = jsonObject.getString("refresh_jwt").isEmpty()? null : jsonObject.getString("refresh_jwt");
                ServiceRegistry.setBlueskySession(session);
                ServiceRegistry.setBlueskyPdsOrigin("https://bsky.social");
                
                BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();
                blueskyClient.getProfile(session);
                if (session.refreshJwt != null) blueskyClient.refreshSession(session, ServiceRegistry.getBlueskyPdsOrigin());
                System.out.println("[INFO][SessionFile][BlueskySessionFile][readSession] Successfully read Bluesky session.");
            } catch (Exception e) {
                System.err.println("[ERROR][SessionFile][BlueskySessionFile][readSession] " + e.getMessage());
                e.printStackTrace();
            }
        }

        public static void deleteSession() {
            File fileSession = new File(fileName);
            if (fileSession.delete()) {
                System.out.println("[INFO][SessionFile][BlueskySessionFile][deleteSession] Successfully deleted Bluesky session.");
            } else {
                System.err.println("[ERROR][SessionFile][BlueskySessionFile][deleteSession] Failed to deleted Bluesky session.");
            }
        }
    }
    
    public class MastodonSessionFile {
        private static String fileName = "mastodon-session.json";
        public static void saveSession() {
            AuthSession session = ServiceRegistry.getMastodonSession();
            try (FileWriter writer = new FileWriter(fileName)) {
                String accessToken = session.accessToken == null? "" : session.accessToken;
                String refreshToken = session.refreshToken == null? "" : session.refreshToken;
                String clientId = session.clientId == null? "" : session.clientId;
                String clientSecret = session.clientSecret == null? "" : session.clientSecret;
                String data = String.format("""
{
    "access_token": "%s",
    "refresh_token": "%s",
    "client_id": "%s",
    "client_secret": "%s"
}
            """, accessToken, refreshToken, clientId, clientSecret);
                writer.write(data);
                System.out.println("[INFO][SessionFile][MastodonSessionFile][saveSession] Successfully saved Mastodon session.");
            } catch (Exception e) {
                System.err.println("[ERROR][SessionFile][MastodonSessionFile][saveSession] Failed to save current Mastodon session. " + e.getMessage());
                e.printStackTrace();
            }
        }

        public static void readSession() {
            try {
                String pathString = System.getProperty("user.dir") + "\\" + fileName;
                Path filePath = Paths.get(pathString);
                String jsonString = String.join("",Files.readAllLines(filePath));
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.getString("access_token").isEmpty()) {
                    System.out.println("[INFO][SessionFile][MastodonSessionFile][readSession] No saved Mastodon session.");
                    return;
                }
                AuthSession session = new AuthSession(null);
                session.accessToken = jsonObject.getString("access_token");
                session.refreshToken = jsonObject.getString("refresh_token");
                session.clientId = jsonObject.getString("client_id");
                session.clientSecret = jsonObject.getString("client_secret");
                session.instanceUrl = "https://mastodon.social";
                
                MastodonClient mastodonClient = ServiceRegistry.getMastodonClient();
                try {
                    mastodonClient.getUserInfo(session);
                } catch (Exception e) {
                    System.err.println("[ERROR][SessionFile][MastodonSessionFile][readSession] " + e.getMessage());
                }

                ServiceRegistry.setMastodonSession(session);
                System.out.println("[INFO][SessionFile][MastodonSessionFile][readSession] Successfully read Mastodon session.");
            } catch (Exception e) {
                System.err.println("[ERROR][SessionFile][MastodonSessionFile][readSession] " + e.getMessage());
                e.printStackTrace();
            }
        }

        public static void deleteSession() {
            File fileSession = new File(fileName);
            if (fileSession.delete()) {
                System.out.println("[INFO][SessionFile][MastodonSessionFile][deleteSession] Successfully deleted Mastodon session.");
            } else {
                System.err.println("[ERROR][SessionFile][MastodonSessionFile][deleteSession] Failed to deleted Mastodon session.");
            }
        }
    }
}
