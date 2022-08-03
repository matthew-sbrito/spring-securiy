package com.techsoft.api.authentication.security.filter;

import com.techsoft.api.authentication.security.token.TokenParser;
import com.techsoft.api.authentication.security.helper.ApplicationSecurityContextHolder;
import com.techsoft.api.common.properties.JwtConfiguration;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.jwt.SignedJWT;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {
    protected final JwtConfiguration jwtConfiguration;
    protected final TokenParser tokenParser;

    public JwtTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenParser tokenParser) {
        this.jwtConfiguration = jwtConfiguration;
        this.tokenParser = tokenParser;
    }

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String header = request.getHeader(jwtConfiguration.getHeader().getName());

        if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

        SignedJWT signedJWT = "signed".equalsIgnoreCase(jwtConfiguration.getType()) ? validate(token) : decryptValidating(token);

        ApplicationSecurityContextHolder.setSecurityContext(signedJWT);

        filterChain.doFilter(request, response);
    }

    @SneakyThrows
    private SignedJWT decryptValidating(String encryptedToken) {
        String signedToken = tokenParser.decryptToken(encryptedToken);
        tokenParser.validateTokenSignature(signedToken);
        return SignedJWT.parse(signedToken);
    }

    @SneakyThrows
    private SignedJWT validate(String signedToken) {
        tokenParser.validateTokenSignature(signedToken);
        return SignedJWT.parse(signedToken);
    }
}
