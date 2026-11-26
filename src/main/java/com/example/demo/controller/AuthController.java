package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.security.CookieUtil;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pheno/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserDto.RegisterResponse> register(@RequestBody UserDto.RegisterRequest request) {
        UserDto.RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginResponse> login(@RequestBody UserDto.LoginRequest request, HttpServletResponse httpResponse) {
        UserDto.LoginResult result = authService.login(request);
        cookieUtil.setRefreshToken(httpResponse, result.refreshToken(), 7 * 24 * 60 * 60);
        return ResponseEntity.status(HttpStatus.OK).body(new UserDto.LoginResponse(result.accessToken(), result.user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        cookieUtil.clearAccessToken(response);
        cookieUtil.clearRefreshToken(response);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }


    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String token = cookieUtil.getRefreshToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found!");
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            String type = claims.get("type", String.class);

            if (!"refresh".equals(type)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token type");
            }
            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid roles");
            }
            String newAccessToken = jwtUtil.generateAccessToken(username, roles);
            cookieUtil.setAccessToken(response, newAccessToken, 15 * 60);
            return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);


        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

    }

}
