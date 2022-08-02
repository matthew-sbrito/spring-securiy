package com.techsoft.api.authentication.security.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.techsoft.api.authentication.domain.ApplicationUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Matheus Brito
 */
@Slf4j
public class SecurityContextUtil {
    private SecurityContextUtil() {
    }

    public static void setSecurityContext(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            System.out.println(claims);

            String username = claims.getSubject();

            if (username == null) {
                throw new JOSEException("Username missing from JWT");
            }

            List<String> authorities = claims.getStringListClaim("authorities");
            ApplicationUser applicationUser = new ApplicationUser();

            applicationUser.setId(claims.getLongClaim("userId"));
            applicationUser.setUsername(username);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    applicationUser, null, createAuthorities(authorities)
            );

            auth.setDetails(signedJWT.serialize());

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.error("Error setting security context ", e);
            SecurityContextHolder.clearContext();
        }
    }

    private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
    }
}

