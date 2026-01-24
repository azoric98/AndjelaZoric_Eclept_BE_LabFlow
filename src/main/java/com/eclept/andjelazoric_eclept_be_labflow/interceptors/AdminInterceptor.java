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

    @Value("${labflow.admin.api-key}")
    private String adminApiKey;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        if (!method.hasMethodAnnotation(AdminOnly.class)) {
            return true;
        }

        String apiKey = request.getHeader("X-ADMIN-KEY");

        if (!adminApiKey.equals(apiKey)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        return true;
    }
}

