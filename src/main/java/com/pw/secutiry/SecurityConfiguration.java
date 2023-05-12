package com.pw.secutiry;

import com.pw.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            "/api/auth/register", // POST
            "/api/auth/authenticate", // POST
            "/authenticate/admin", // POST

            "/api/product/search-by-string", // PUT
            "/api/product/{id}", // GET
            "/api/product/all", // GET
            "/api/product/filter/{query}", // GET,

            "api/collection/all", // GET
            "api/collection/{name}", // GET
    };

    private final String[] ADMIN_ENDPOINTS = {
            "/api/admin/**",
            "/api/product/", // POST
            "/api/product/", // PUT
            "/api/product/{id}", // DELETE
            "/api/collection/", // PUT
            "/api/admin/all-customers", // GET
            "/api/admin/update-customer", // PUT
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


                // White list API
                .requestMatchers(HttpMethod.POST, "/api/auth/register")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/authenticate")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/authenticate/admin")
                .permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/product/search-by-string")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/product/{id}")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/product/all")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/product/filter/{query}")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/collection/all")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/collection/{name}")
                .permitAll()


                // After authentication, any admin API requires authenticated user to have an admin role
                .requestMatchers("/api/admin/**")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/product/")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/product/")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/product/{id}")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/collection/")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/all-customers")
                .hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/update-customer")
                .hasAuthority("ADMIN")




                // Any API is not whitelist => Require authentication
                .anyRequest()
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
