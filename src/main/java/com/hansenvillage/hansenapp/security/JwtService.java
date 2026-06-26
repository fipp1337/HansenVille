package com.hansenvillage.hansenapp.security;

import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.Role;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    private final byte[] secret;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${store.jwt.secret}") String secret,
            @Value("${store.jwt.expiration-ms}") long expirationMs,
            @Value("${store.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateToken(Family family, List<Role> roles) {
        try {
            Date now = new Date();
            List<String> roleNames = roles.stream().map(Role::name).toList();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(family.getId()))
                    .claim("email", family.getEmail())
                    .claim("roles", roleNames)
                    .issueTime(now)
                    .expirationTime(new Date(now.getTime() + expirationMs))
                    .build();

            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJwt.sign(new MACSigner(secret));
            return signedJwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to generate JWT", e);
        }
    }

    public String generateRefreshToken(Family family, List<Role> roles) {
        try {
            Date now = new Date();
            List<String> roleNames = roles.stream().map(Role::name).toList();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(family.getId()))
                    .claim("email", family.getEmail())
                    .claim("roles", roleNames)
                    .claim("type", "refresh")
                    .issueTime(now)
                    .expirationTime(new Date(now.getTime() + refreshExpirationMs))
                    .build();

            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJwt.sign(new MACSigner(secret));
            return signedJwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to generate Refresh JWT", e);
        }
    }

    public SecurityFamily parseRefreshToken(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            if (!signedJwt.verify(new MACVerifier(secret))) {
                throw FamilyException.of(FamilyErrorCode.INVALID_TOKEN);
            }

            JWTClaimsSet claims = signedJwt.getJWTClaimsSet();

            String type = claims.getStringClaim("type");
            if (!"refresh".equals(type)) {
                throw FamilyException.of(FamilyErrorCode.INVALID_TOKEN);
            }

            Date expiration = claims.getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                throw FamilyException.of(FamilyErrorCode.INVALID_TOKEN);
            }

            UUID id = UUID.fromString(claims.getSubject());
            String email = claims.getStringClaim("email");
            List<Role> roles = claims.getStringListClaim("roles").stream()
                    .map(Role::valueOf)
                    .toList();

            Family family = new Family();
            family.setId(id);
            family.setEmail(email);
            family.setPassword("");

            return new SecurityFamily(family, roles);
        } catch (ParseException | JOSEException e) {
            throw FamilyException.of(FamilyErrorCode.INVALID_TOKEN);
        }
    }
    public SecurityFamily parseToken(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            if (!signedJwt.verify(new MACVerifier(secret))) {
                throw FamilyException.of(FamilyErrorCode.INVALID_TOKEN);
            }

            JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
            Date expiration = claims.getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                throw FamilyException.of(FamilyErrorCode.INVALID_TOKEN);
            }

            UUID id = UUID.fromString(claims.getSubject());
            String email = claims.getStringClaim("email");
            List<Role> roles = claims.getStringListClaim("roles").stream()
                    .map(Role::valueOf)
                    .toList();

            Family family = new Family();
            family.setId(id);
            family.setEmail(email);
            family.setPassword("");
            return new SecurityFamily(family, roles);
        } catch (ParseException | JOSEException e) {
            throw FamilyException.of(FamilyErrorCode.INVALID_TOKEN);
        }


    }

}
