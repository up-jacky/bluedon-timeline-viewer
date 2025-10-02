package oauthServices;

import java.net.URI;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
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
        System.out.println("Signed JWT generated")
        return signedJWT.serialize();
    }
}
