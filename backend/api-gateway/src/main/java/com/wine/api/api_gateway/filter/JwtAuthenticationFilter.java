package com.wine.api.api_gateway.filter;

import com.wine.api.api_gateway.service.JwtAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Request uri {}", request.getRequestURI());

        if (isPermittedUri(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Starting jwt filter");

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid");
            return;
        }

        jwt = authHeader.substring(7);

        if (jwtAuthService.isTokenValid(jwt)) {

            String userEmail = jwtAuthService.extractUserEmail(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(userEmail, "", new ArrayList<>());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());

                authToken.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

            filterChain.doFilter(request, response);
        }
    }

    private boolean isPermittedUri(String uri) {
        return uri.equals("/auth/login") ||
                uri.equals("/api/v1/auth/login") ||
                uri.equals("/actuator/health") ||
                uri.equals("/actuator/metrics") ||
                uri.startsWith("/actuator/metrics/") ||
                uri.startsWith("/api/monitor/");
    }
}