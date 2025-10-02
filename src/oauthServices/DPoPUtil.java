package oauthServices;

import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
    public static String buildDPoP(String method, String url, String nonce, KeyPair keyPair) throws Exception {
        Instant now = Instant.now();
        URI uri = new URI(url);

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .issueTime(java.util.Date.from(now))
                .claim("htm", method)      // HTTP method
                .claim("htu", uri.toString()); // HTTP URL

        if (nonce != null) {
            claims.claim("nonce", nonce);
        }

        JWTClaimsSet claimSet = claims.build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(new JOSEObjectType("dpop+jwt"))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimSet);
        signedJWT.sign(new ECDSASigner((ECPrivateKey) keyPair.getPrivate()));
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

        // 2. Get x and y coordinates
        byte[] xBytes = pub.getW().getAffineX().toByteArray();
        byte[] yBytes = pub.getW().getAffineY().toByteArray();

        // Ensure unsigned (remove leading 0 if present)
        if (xBytes[0] == 0) xBytes = java.util.Arrays.copyOfRange(xBytes, 1, xBytes.length);
        if (yBytes[0] == 0) yBytes = java.util.Arrays.copyOfRange(yBytes, 1, yBytes.length);

        // 3. Base64URL encode
        String xB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(xBytes);
        String yB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(yBytes);

        System.out.println("x: " + xB64);
        System.out.println("y: " + yB64);
        System.out.println("Use these values in your client metadata under jwks.keys[0].x and jwks.keys[0].y");
    }
}

   
}