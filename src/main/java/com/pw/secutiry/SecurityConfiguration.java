package com.pw.secutiry;

import com.pw.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final String[] WHITE_LIST_ENDPOINTS = {
            /*"/api/product/**",*/
            "/**"
    };

    private final String[] ADMIN_ENDPOINTS = {
            /*"/api/product/**",*/
            "/api/admin"
    };

    @Autowired
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(WHITE_LIST_ENDPOINTS) // Whitelist api
                .permitAll()
                .anyRequest() // All the request not in whitelist => Require authenticated
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider) // Encoding password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Execute the JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter

        return http.build();
    }
}
