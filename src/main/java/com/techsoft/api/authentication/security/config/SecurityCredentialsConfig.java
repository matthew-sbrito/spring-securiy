package com.techsoft.api.authentication.security.config;

import com.techsoft.api.authentication.security.filter.JwtTokenAuthorizationFilter;
import com.techsoft.api.authentication.security.token.TokenBuilder;
import com.techsoft.api.authentication.security.token.TokenParser;
import com.techsoft.api.common.properties.JwtConfiguration;
import com.techsoft.api.authentication.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityCredentialsConfig {
    private final UserDetailsService userDetailsService;
    private final JwtConfiguration jwtConfiguration;
    private final TokenBuilder tokenBuilder;
    private final TokenParser tokenParser;

    @Autowired
    public SecurityCredentialsConfig(
            UserDetailsService userDetailsService, JwtConfiguration jwtConfiguration, TokenBuilder tokenBuilder, TokenParser tokenParser
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtConfiguration = jwtConfiguration;
        this.tokenBuilder = tokenBuilder;
        this.tokenParser = tokenParser;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SneakyThrows
    public AuthenticationManager buildAuthenticationManager(HttpSecurity httpSecurity) {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = buildAuthenticationManager(httpSecurity);
        JwtUsernameAndPasswordAuthenticationFilter filterGenerateJWT = new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager, jwtConfiguration, tokenBuilder);
        JwtTokenAuthorizationFilter filterParserJWT = new JwtTokenAuthorizationFilter(jwtConfiguration, tokenParser);

        httpSecurity
                .csrf().disable()
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling()
                .authenticationEntryPoint((request, response, authenticationException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .and().addFilter(filterGenerateJWT)
                .addFilterAfter(filterParserJWT, UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .authorizeRequests()
                .antMatchers(jwtConfiguration.getLoginURL()).permitAll()
                .anyRequest().authenticated();

        return httpSecurity.build();
    }

}
