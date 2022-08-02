package com.techsoft.api.authentication.security.token;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.techsoft.api.authentication.domain.ApplicationUser;
import com.techsoft.api.common.properties.JwtConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class TokenBuilder {

    private final JwtConfiguration jwtConfiguration;

    @Autowired
    public TokenBuilder(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }

    @SneakyThrows
    public SignedJWT createSignedJWT(Authentication auth) {
        log.info("Starting to create the signed JWT");

        ApplicationUser applicationUser = (ApplicationUser) auth.getPrincipal();

        JWTClaimsSet jwtClaimsSet = createJWTClaimSet(auth, applicationUser);

        KeyPair rsaKeys = generateKeyPair();

        log.info("Building JWK from the RSA Keys");

        JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic())
                .keyID(UUID.randomUUID().toString())
                .build();

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .jwk(jwk)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);

        log.info("Signing the token with the private RSA Key");

        RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());

        signedJWT.sign(signer);

        log.info("Serialized token '{}'", signedJWT.serialize());

        return signedJWT;
    }

    private JWTClaimsSet createJWTClaimSet(Authentication auth, ApplicationUser applicationUser) {
        log.info("Starting to create the signed JWT");

        List<String> authorityList = auth.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(toList());

        Date expiration = new Date(System.currentTimeMillis() + ( jwtConfiguration.getExpiration() * 1000 ));

        return new JWTClaimsSet.Builder()
                .subject(applicationUser.getUsername())
                .claim("authorities", authorityList)
                .claim("userId", applicationUser.getId())
                .issuer("http://techsoft.org")
                .issueTime(new Date())
                .expirationTime(expiration)
                .build();
    }

    @SneakyThrows
    private KeyPair generateKeyPair() {
        log.info("Generating RSA 2048 bits Keys");

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);

        return generator.genKeyPair();

    }

    public String encryptToken(SignedJWT signedJWT) throws JOSEException {
        log.info("Starting the encrypt token method");

        log.info("Secret");
        log.info(jwtConfiguration.getSecret());
        log.info("Secret bytes");
        log.info(String.valueOf(jwtConfiguration.getSecret().getBytes().length));

        DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getSecret().getBytes());

        JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT").build();

        JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJWT));

        log.info("Encrypting token with system's private key");

        jweObject.encrypt(directEncrypter);

        log.info("Token encrypted");

        return jweObject.serialize();

    }
}
