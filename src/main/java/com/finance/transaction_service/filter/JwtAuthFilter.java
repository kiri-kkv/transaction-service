package com.finance.transaction_service.filter;

import com.finance.transaction_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader =request.getHeader("Authorization");
        if(authHeader==null || !authHeader.contains("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);
        String userName = jwtService.extractUsername(token);

    }
}
