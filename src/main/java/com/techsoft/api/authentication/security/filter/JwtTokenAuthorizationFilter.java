package com.techsoft.api.authentication.security.filter;

import com.nimbusds.jwt.SignedJWT;
import com.techsoft.api.authentication.security.token.TokenParser;
import com.techsoft.api.authentication.security.util.SecurityContextUtil;
import com.techsoft.api.common.properties.JwtConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {
    protected final JwtConfiguration jwtConfiguration;
    protected final TokenParser tokenParser;

    @Autowired
    public JwtTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenParser tokenParser) {
        this.jwtConfiguration = jwtConfiguration;
        this.tokenParser = tokenParser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(jwtConfiguration.getHeader().getName());

        if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

        SignedJWT signedJWT = "signed".equalsIgnoreCase(jwtConfiguration.getType()) ? validate(token) : decryptValidating(token);

        SecurityContextUtil.setSecurityContext(signedJWT);

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
