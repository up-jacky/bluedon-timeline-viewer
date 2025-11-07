package com.bluedon.services;

import com.bluedon.utils.Http;
import com.bluedon.utils.SessionFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


public class BlueskyClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void createSession(AuthSession session, String pdsOrigin, String password, String handle) throws Exception {
        if (handle == null || handle.isBlank() || password == null || password.isBlank()) {
            throw new IOException("Missing Credentials: Handle or Password is missing.");
        }

        String url = pdsOrigin + "/xrpc/com.atproto.server.createSession";

        Map<String, Object> body = new HashMap<>();
        body.put("identifier", handle);
        body.put("password", password);

        String jsonBody = MAPPER.writeValueAsString(body);

        Map<String, String> headers = Map.of(
                "Content-Type", "application/json"
        );

        HttpResponse<String> response = Http.postFormWithResponse(url, headers, jsonBody);
        JSONObject responseBody = new JSONObject(response.body());

        if (response.statusCode() == 200) {
            System.out.println("[INFO][BlueskyClient][createSession] Sucessful creating session!");
            session.accessJwt = responseBody.getString("accessJwt");
            session.refreshJwt = responseBody.getString("refreshJwt");
            session.did = responseBody.getString("did");
        } else {
            System.err.println("[ERROR][BlueskyClient][createSession]  Error creating session!");
            throw new Exception(response.statusCode() + " " + responseBody.getString("error") + ": " + responseBody.getString("message"));
        }
    }

    public void refreshSession(AuthSession session, String pdsOrigin) throws Exception {
        if (session.refreshJwt == null || session.refreshJwt.isBlank()) {
            throw new IllegalStateException("Unauthorized Session: Session has no 'refreshJwt' or 'refreshJwt' is empty.");
        }

        String url = pdsOrigin + "/xrpc/com.atproto.server.refreshSession";

        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + session.refreshJwt,
                "Content-Type", "application/json"
        );

        HttpResponse<String> response = Http.postFormWithResponse(url, headers, "");
        JSONObject responseBody = new JSONObject(response.body());

        if (response.statusCode() == 200) {
            System.out.println("[INFO][BlueskyClient][refreshSession] Successful refreshing session!");
            session.accessJwt = responseBody.getString("accessJwt");
            session.refreshJwt = responseBody.getString("refreshJwt");
            session.did = responseBody.getString("did");
            SessionFile.BlueskySessionFile.saveSession();
        } else if (response.statusCode() == 400) {
            System.out.println("[INFO][BlueskyClient][refreshSession] Session has expired. Deleting saved session...");
            ServiceRegistry.setBlueskySession(null);
            SessionFile.BlueskySessionFile.deleteSession();
        } else {
            System.err.println("[ERROR][BlueskyClient][refreshSession] Failed to refresh session!");
            throw new Exception(response.statusCode() + " " + responseBody.getString("error") + ": " + responseBody.getString("message"));
        }
    }

    public void deleteSession(AuthSession session, String pdsOrigin) throws Exception {
        if (session.refreshJwt == null || session.refreshJwt.isBlank()) {
            throw new IllegalStateException("Unauthorized Session: Session has no 'refreshJwt' or 'refreshJwt' is empty.");
        }

        String url = pdsOrigin + "/xrpc/com.atproto.server.deleteSession";

        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + session.refreshJwt,
                "Content-Type", "application/json"
        );

        HttpResponse<String> response = Http.postFormWithResponse(url, headers, "");

        if (response.statusCode() == 200) {
            System.out.println("[INFO][BlueskyClient][deleteSession] Successful deleting session!");
        } else {
            JSONObject responseBody = new JSONObject(response.body());
            System.err.println("[ERROR][BlueskyClient][deleteSession] Failed to delete session!");
            throw new Exception(response.statusCode() + " " + responseBody.getString("error") + ": " + responseBody.getString("message"));
        }
    }

    public void getProfile(AuthSession session, String pdsOrigin) throws Exception {
        if (session.did == null || session.did.isBlank()) {
            throw new IllegalStateException("Unauthorized Session: Session has no 'did' or 'did' is empty.");
        }

        String url = pdsOrigin + "/xrpc/app.bsky.actor.getProfile?actor=" + session.did;
        
        if (session.accessJwt == null || session.accessJwt.isBlank()) {
            throw new IllegalStateException("Unauthorized Session: Session has no 'accessJwt' or 'accessJwt' is empty.");
        }

        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + session.accessJwt,
                "Content-Type", "application/json"
        );

        HttpResponse<String> response = Http.getWithResponse(url, headers);
        JSONObject responseBody = new JSONObject(response.body());

        if (response.statusCode() == 200) {
            System.out.println("[INFO][BlueskyClient][getProfile] Successful getting user profile!");
            session.handle = responseBody.getString("handle");
            session.displayName = (String) responseBody.get("displayName");
            if(session.displayName == null || session.displayName.isEmpty()) {    
                System.out.println("[INFO][BlueskyClient][getProfile] User has no 'displayName'. Setting its value to 'handle'.");
                session.displayName = session.handle;
            }
            session.avatarUri = (String) responseBody.get("avatar");
            session.profileUrl = "https://bsky.app/profile/" + session.handle;
        } else {
            System.err.println("[ERROR][BlueskyClient][getProfile] Failed to get profile!");
            throw new Exception(response.statusCode() + " " + responseBody.getString("error") + ": " + responseBody.getString("message"));
        }
    }

    public JSONObject getTimeline(AuthSession session, String pdsOrigin, int limit) throws Exception {
        if (session.accessJwt == null || session.accessJwt.isBlank()) {
            throw new IllegalStateException("Unauthorized Session: Session has no 'accessJwt' or 'accessJwt' is empty.");
        }

        String url = pdsOrigin + "/xrpc/app.bsky.feed.getTimeline?limit=" + limit;

        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + session.accessJwt,
                "Content-Type", "application/json"
        );

        HttpResponse<String> response = Http.getWithResponse(url, headers);
        JSONObject responseBody = new JSONObject(response.body());

        if (response.statusCode() == 200) {
            System.out.println("[INFO][BlueskyClient][getTimeline] Successful getting user timeline!");
            return responseBody;
        } else {
            System.err.println("[ERROR][BlueskyClient][getTimeline] Failed to get user timeline!");
            throw new Exception(response.statusCode() + " " + responseBody.getString("error") + ": " + responseBody.getString("message"));
        }
    }
}
