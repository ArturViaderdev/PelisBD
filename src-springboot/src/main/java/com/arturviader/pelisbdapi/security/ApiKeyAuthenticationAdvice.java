package com.arturviader.pelisbdapi.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

/*@ControllerAdvice
public class ApiKeyAuthenticationAdvice {

    @Autowired
    private ApiKeyValidator apiKeyValidator;

    @ModelAttribute
    public void validateApiKey(HttpServletRequest request, HttpServletResponse response) {
        String key = request.getHeader("X-API-Key");
        if (key == null || !apiKeyValidator.isValid(key)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                response.getWriter().write("{\"error\":\"Unauthorized: Invalid or missing API Key\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}*/
