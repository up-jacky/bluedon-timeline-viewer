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

/**
 * Handles reading, saving, and deleting *-session.json files.
 */
public class SessionFile {
    /**
     * Reads session for both Bluesky and Mastodon.
     */
    public static void readSession() {
        BlueskySessionFile.readSession();
        MastodonSessionFile.readSession();
    }

    /**
     * Handles reading, saving, and deleting bluesky-session.json
     */
    public class BlueskySessionFile {
        private static String fileName = "bluesky-session.json";

        /**
         * If {@code bluesky-session.json} does not exist, it creates and saves the current session 
         * from the {@link ServiceRegistry#getBlueskySession()} and save its {@code did},
         * {@code accessJwt}, and {@code refreshJwt}. Else, it overwrite the contents of the
         * {@code bluesky-session.json}.
         */
        public static void saveSession() {
            AuthSession session = ServiceRegistry.getBlueskySession();
            try (FileWriter writer = new FileWriter(fileName)) {
                String accessJwt = session.accessJwt == null? "" : session.accessJwt;
                String refreshJwt = session.refreshJwt == null? "" : session.refreshJwt;
                String did = session.did == null? "" : session.did;
                String data = String.format("""
{
    "did": "%s",
    "access_jwt": "%s",
    "refresh_jwt": "%s",
}
            """, did, accessJwt, refreshJwt);
                writer.write(data);
                System.out.println("[INFO][SessionFile][BlueskySessionFile][saveSession] Successfully saved Bluesky session.");
                Toast.success.showToast("Successfully saved Bluesky session!");
            } catch (Exception e) {
                System.err.println("[ERROR][SessionFile][BlueskySessionFile][saveSession] Failed to save current Bluesky session. " + e.getMessage());
                Toast.error.showToast("Failed to save Bluesky session! Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * If {@code bluesky-session.json} exists, it reads its content
         * and save it as a current session in {@link ServiceRegistry#setBlueskySession(AuthSession)}.
         */
        public static void readSession() {
            try {
                String pathString = System.getProperty("user.dir") + "\\" + fileName;
                Path filePath = Paths.get(pathString);
                String jsonString = String.join("",Files.readAllLines(filePath));
                JSONObject jsonObject = new JSONObject(jsonString);
                AuthSession session = new AuthSession();

                session.did = jsonObject.getString("did");
                session.accessJwt = jsonObject.getString("access_jwt").isEmpty()? null : jsonObject.getString("access_jwt");
                session.refreshJwt = jsonObject.getString("refresh_jwt").isEmpty()? null : jsonObject.getString("refresh_jwt");
                ServiceRegistry.setBlueskySession(session);
                
                BlueskyClient blueskyClient = ServiceRegistry.getBlueskyClient();
                blueskyClient.getProfile(session, ServiceRegistry.getBlueskyPdsOrigin());
                if (session.refreshJwt != null) blueskyClient.refreshSession(session, ServiceRegistry.getBlueskyPdsOrigin());
                System.out.println("[INFO][SessionFile][BlueskySessionFile][readSession] Successfully read Bluesky session.");
                Toast.success.showToast("Successfully read Bluesky session!");
            } catch (Exception e) {
                if(e.getMessage().equals(System.getProperty("user.dir")  + "\\" + fileName)) return;
                System.err.println("[ERROR][SessionFile][BlueskySessionFile][readSession] Error:" + e.getMessage());
                Toast.error.showToast("Failed to read Bluesky session! Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Deletes the {@code bluesky-session.json} file.
         */
        public static void deleteSession() {
            File fileSession = new File(fileName);
            if (fileSession.delete()) {
                System.out.println("[INFO][SessionFile][BlueskySessionFile][deleteSession] Successfully deleted Bluesky session.");
                Toast.success.showToast("Successfully deleted Bluesky session!");
            } else {
                System.err.println("[ERROR][SessionFile][BlueskySessionFile][deleteSession] Failed to delete Bluesky session.");
                Toast.error.showToast("Failed to delete Bluesky session!");
            }
        }
    }
    
    /**
     * Handles reading, saving, and deleting mastodon-session.json
     */
    public class MastodonSessionFile {
        private static String fileName = "mastodon-session.json";
        
        /**
         * If {@code mastodon-session.json}, it creates and saves the current session 
         * from the {@link ServiceRegistry#getMastodonSession()} and save its {@code accessToken},
         * {@code refreshToken}, {@code clientId}, and {@code clientSecret}.
         */
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
                Toast.success.showToast("Successfully saved Mastodon session!");
            } catch (Exception e) {
                System.err.println("[ERROR][SessionFile][MastodonSessionFile][saveSession] Failed to save current Mastodon session. " + e.getMessage());
                Toast.error.showToast("Failed to save Mastodon session! Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * If {@code mastodon-session.json} exists, it reads its content
         * and save it as a current session in {@link ServiceRegistry#setMastodonSession(AuthSession)}.
         */
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
                AuthSession session = new AuthSession();
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
                Toast.success.showToast("Successfully read Mastodon session!");
            } catch (Exception e) {
                if(e.getMessage().equals(System.getProperty("user.dir")  + "\\" + fileName)) return;
                System.err.println("[ERROR][SessionFile][MastodonSessionFile][readSession] Error:" + e.getMessage());
                Toast.error.showToast("Failed to read Mastodon session! Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Deletes the {@code mastodon-session.json} file.
         */
        public static void deleteSession() {
            File fileSession = new File(fileName);
            if (fileSession.delete()) {
                System.out.println("[INFO][SessionFile][MastodonSessionFile][deleteSession] Successfully deleted Mastodon session.");
                Toast.success.showToast("Successfully deleted Mastodon session!");
            } else {
                System.err.println("[ERROR][SessionFile][MastodonSessionFile][deleteSession] Failed to delete Mastodon session.");
                Toast.error.showToast("Failed to delete Mastodon session!");
            }
        }
    }
}
