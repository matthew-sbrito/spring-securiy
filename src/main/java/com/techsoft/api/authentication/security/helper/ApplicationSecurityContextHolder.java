package com.techsoft.api.authentication.security.helper;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.techsoft.api.authentication.domain.ApplicationUser;
import com.techsoft.api.authentication.service.UserDetailsServiceImpl;
import com.techsoft.api.common.helper.StaticContextAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Matheus Brito
 */
@Slf4j
public class ApplicationSecurityContextHolder {
    private ApplicationSecurityContextHolder() { }

    private static UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken(String username) {
        UserDetailsService userDetailsService = StaticContextAccessor.getBean(UserDetailsServiceImpl.class);
        ApplicationUser applicationUser = (ApplicationUser) userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                applicationUser, applicationUser.getPassword(), applicationUser.getAuthorities()
        );
    }

    public static void setSecurityContext(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            String username = claims.getSubject();

            if (username == null) {
                throw new JOSEException("Username missing from JWT");
            }

            UsernamePasswordAuthenticationToken auth = usernamePasswordAuthenticationToken(username);

            auth.setDetails(signedJWT.serialize());

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.error("Error setting security context ", e);
            SecurityContextHolder.clearContext();
        }
    }

    public static ApplicationUser getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (ApplicationUser) auth.getPrincipal();
    }
}

