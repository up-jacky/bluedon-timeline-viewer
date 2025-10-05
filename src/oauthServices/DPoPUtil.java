package oauthServices;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class DPoPUtil {

    // Build DPoP JWT for given method and url
    public static String buildDPoP(String method, String url, String nonce, KeyPair keyPair, String accessToken) throws Exception {
        Instant now = Instant.now();
        URI uri = new URI(url);

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
            .jwtID(UUID.randomUUID().toString())
            .issueTime(java.util.Date.from(now))
            .expirationTime(java.util.Date.from(now.plusSeconds(120))) // Add exp claim (2 min expiry)
            .claim("htm", method)
            .claim("htu", uri.toString());
        if (nonce != null) {
            claims.claim("nonce", nonce);
        }
         // Add ath (access token hash) if access token is provided
        if (accessToken != null) {
            String ath = calculateAccessTokenHash(accessToken);
            claims.claim("ath", ath);
        }

        JWTClaimsSet claimSet = claims.build();

        // Build ECKey (JWK) from keyPair
        ECPublicKey pubKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privKey = (ECPrivateKey) keyPair.getPrivate();
        com.nimbusds.jose.jwk.ECKey ecJWK = new com.nimbusds.jose.jwk.ECKey.Builder(
                com.nimbusds.jose.jwk.Curve.P_256, pubKey)
                .privateKey(privKey)
                .keyID("dksadjakdjakdjsakdayawkona")
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(new JOSEObjectType("dpop+jwt"))
                .jwk(ecJWK.toPublicJWK()) // Pass the public JWK as a JSON object
                .keyID(ecJWK.getKeyID())
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimSet);
        signedJWT.sign(new ECDSASigner(privKey));
        System.out.println("Signed JWT generated");
        return signedJWT.serialize();
    }
    public class JWKGenerator {
    public static void main(String[] args) throws Exception {
        // 1. Generate EC P-256 keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        KeyPair kp = kpg.generateKeyPair();

        ECPublicKey pub = (ECPublicKey) kp.getPublic();
        ECPrivateKey priv = (ECPrivateKey) kp.getPrivate();

        // 2. Extract affine coordinates (x, y)
        byte[] xBytes = pub.getW().getAffineX().toByteArray();
        byte[] yBytes = pub.getW().getAffineY().toByteArray();

        // Ensure unsigned (strip leading zero if present)
        if (xBytes[0] == 0) xBytes = java.util.Arrays.copyOfRange(xBytes, 1, xBytes.length);
        if (yBytes[0] == 0) yBytes = java.util.Arrays.copyOfRange(yBytes, 1, yBytes.length);

        // 3. Private scalar d
        byte[] dBytes = priv.getS().toByteArray();
        if (dBytes[0] == 0) dBytes = java.util.Arrays.copyOfRange(dBytes, 1, dBytes.length);

        // 4. Base64URL encode (RFC 7515)
        String xB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(xBytes);
        String yB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(yBytes);
        String dB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(dBytes);

        System.out.println("{");
        System.out.println("  \"kty\": \"EC\",");
        System.out.println("  \"crv\": \"P-256\",");
        System.out.println("  \"x\": \"" + xB64 + "\",");
        System.out.println("  \"y\": \"" + yB64 + "\",");
        System.out.println("  \"d\": \"" + dB64 + "\"");
        System.out.println("}");
    }
    
}
/**
     * Calculate SHA-256 hash of access token and encode as base64url (without padding)
     * This is required for the "ath" claim in DPoP proofs when using an access token
     */
    private static String calculateAccessTokenHash(String accessToken) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(accessToken.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
    }

   
}