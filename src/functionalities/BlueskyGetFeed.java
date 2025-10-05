package functionalities;

import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauthServices.DPoPUtil;
import oauthServices.HttpUtil;
import oauthServices.JsonUtil;

public class BlueskyGetFeed {

    public static List<Map<String, Object>> fetchBlueskyFeed(application.Main.BlueskyOAuth.AuthSession session, KeyPair dpopKeyPair) throws Exception {
    // 1. Resolve DID to get PDS endpoint
    String did = session.did;
    String didDocUrl = "https://plc.directory/" + did;
    String didDocJson = HttpUtil.get(didDocUrl, null);
    Map<String, Object> didDoc = JsonUtil.fromJson(didDocJson);

    String pdsEndpoint = null;
    if (didDoc.containsKey("service")) {
        List<Map<String, Object>> services = (List<Map<String, Object>>) didDoc.get("service");
        for (Map<String, Object> svc : services) {
            Object typeObj = svc.get("type");
            if (typeObj != null && (
                    "atproto_pds".equalsIgnoreCase(typeObj.toString()) ||
                    "AtprotoPersonalDataServer".equalsIgnoreCase(typeObj.toString())
                )) {
                pdsEndpoint = (String) svc.get("serviceEndpoint");
                break;
            }
        }
    }
    if (pdsEndpoint == null) throw new Exception("Could not resolve PDS endpoint for DID: " + did);

    String apiUrl = "https://api.bsky.app/xrpc/app.bsky.feed.getTimeline";
    // IMPORTANT: Start with null nonce for the PDS (different server than token endpoint)
    String pdsNonce = null;
     // If we don't have a nonce yet, get one from the server first
    Map<String, String> headers = new HashMap<>();
    int maxTries = 3;
    for (int attempt = 0; attempt < maxTries; attempt++) {
        headers.put("Authorization", "DPoP " + session.accessToken);
        headers.put("DPoP", DPoPUtil.buildDPoP("GET", apiUrl, pdsNonce, dpopKeyPair, session.accessToken));
        HttpResponse<String> response = HttpUtil.getWithResponse(apiUrl, headers);

         System.out.println("Response status: " + response.statusCode());
        
        // Extract nonce from response header
        String newNonce = HttpUtil.extractDpopNonce(response);
        if (newNonce != null) {
            pdsNonce = newNonce;
            System.out.println("Updated PDS nonce: " + pdsNonce);
        }
        
        // Check if successful
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Map<String, Object> json = JsonUtil.fromJson(response.body());
            return (List<Map<String, Object>>) json.get("feed");
        }



        Map<String, Object> json = JsonUtil.fromJson(response.body());
        if (newNonce == null && json.containsKey("nonce")) {
            newNonce = (String) json.get("nonce");
        }
        if (newNonce != null) session.dpopNonce = newNonce;

        System.out.println("Json: " + json);
        System.out.println("newNonce: " + newNonce);
        System.out.println("Json error: " + json.get("error"));

        if (!"use_dpop_nonce".equals(json.get("error"))) {
            // Success or other error
            return (List<Map<String, Object>>) json.get("feed");
        } else {
            System.out.println("Retrying with new nonce: " + newNonce);
        }
    }
    throw new Exception("Failed to fetch feed after nonce retries.");
}
}