package com.example.demo.security;

import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.JwtException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;

	@Override
	public boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.startsWith("/pheno/auth/login") || uri.startsWith("/pheno/auth/logout");
	}
	
	@Override
	public void doFilterInternal(HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
			throws ServletException, IOException {
			// Prefer Authorization header; fall back to HTTP-only cookie
			String authHeader = request.getHeader("Authorization");
			String token = (authHeader != null && authHeader.startsWith("Bearer "))
					? authHeader.substring(7)
					: cookieUtil.getAccessToken(request);

		if (token !=null && SecurityContextHolder.getContext().getAuthentication() == null){
			try{
				Claims claims = jwtUtil.parseToken(token);
				String type = claims.get("type", String.class);

				if (!"access".equals(type)){
					 filterChain.doFilter(request, response);
					 return;
				}
				String username = claims.getSubject();

				List<String> roles = claims.get("roles", List.class);
				if (roles==null){
					roles = List.of();
				}
				List<SimpleGrantedAuthority> authorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r))
						.toList();
				Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}catch (JwtException e){
				SecurityContextHolder.clearContext();
			}

		}

		filterChain.doFilter(request, response);
	}
}
