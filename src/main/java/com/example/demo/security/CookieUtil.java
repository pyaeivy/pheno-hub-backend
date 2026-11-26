package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;

@Component
public class CookieUtil {

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    public String getAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "access_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public void setAccessToken(HttpServletResponse response, String token, int maxAge) {
        response.addHeader("Set-Cookie", buildCookie("access_token", token, "/", maxAge).toString());
    }

    /** Refresh token is scoped to /pheno/auth/refresh — not sent on every request. */
    public void setRefreshToken(HttpServletResponse response, String token, int maxAge) {
        response.addHeader("Set-Cookie", buildCookie("refresh_token", token, "/pheno/auth/refresh", maxAge).toString());
    }

    public void clearAccessToken(HttpServletResponse response) {
        response.addHeader("Set-Cookie", buildCookie("access_token", "", "/", 0).toString());
    }

    public void clearRefreshToken(HttpServletResponse response) {
        response.addHeader("Set-Cookie", buildCookie("refresh_token", "", "/pheno/auth/refresh", 0).toString());
    }

    private ResponseCookie buildCookie(String name, String value, String path, int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path(path)
                .maxAge(maxAge)
                .build();
    }
}

