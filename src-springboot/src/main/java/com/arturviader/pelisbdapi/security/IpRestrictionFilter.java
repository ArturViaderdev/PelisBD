package com.arturviader.pelisbdapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*@Component
public class IpRestrictionFilter extends OncePerRequestFilter {

    @Value("${app.frontend.ip}")
    private String ALLOWED_IP;

    public IpRestrictionFilter(Environment environment) {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();

        if (!ALLOWED_IP.equals(clientIp)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "IP no permitida");
            return;
        }

        filterChain.doFilter(request, response);
    }
}*/