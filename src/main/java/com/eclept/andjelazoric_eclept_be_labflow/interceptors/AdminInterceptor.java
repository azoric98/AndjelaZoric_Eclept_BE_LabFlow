package com.eclept.andjelazoric_eclept_be_labflow.interceptors;

import com.eclept.andjelazoric_eclept_be_labflow.annotation.AdminOnly;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private static final String ADMIN_HEADER = "X-ADMIN-KEY";

    @Value("${labflow.admin.api-key}")
    private String adminApiKey;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        if (!method.hasMethodAnnotation(AdminOnly.class)) {
            return true;
        }

        String apiKey = request.getHeader(ADMIN_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        if (!adminApiKey.equals(apiKey)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        return true;
    }
}

