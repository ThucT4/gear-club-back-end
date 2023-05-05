package com.pw.secutiry;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check authHeader have jwt or not
        // Jwt always start with "Bearer "
        // If jwt is not sent with the header => return
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Calling UsernamePasswordAuthenticationFilter
            // Not have SecurityContextHolder => Can not pass this filter
            filterChain.doFilter(request, response);
            return;
        }

        // If jwt is found within the header, call JWTService to extract user's email from jwt
        jwt = authHeader.substring(7); // pass first "Bearer "
        userEmail = jwtService.extractUsername(jwt);

        // If  user's email can be extracted from jwt
        // and the user is not connected (authenticated) before
        // => Get user's details from database
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // If user is found => Save to userDetails variable otherwise through exception
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Validate jwt, if valid => Update security context holder
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken) ;
            }
        }

        // After set SecurityContextHolder (user detail)
        // Calling UsernamePasswordAuthenticationFilter
        // UsernamePasswordAuthenticationFilter uses SecurityContextHolder to verify user
        filterChain.doFilter(request, response);
    }
}
