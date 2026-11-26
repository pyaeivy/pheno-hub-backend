package com.example.demo.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.Filter;

@Configuration
public class SecurityConfig {

	@Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:4000}")
	private String allowedOrigins;
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager manager(AuthenticationConfiguration config){
		return config.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity security, JwtAuthFilter filter){
		security.csrf(AbstractHttpConfigurer::disable)
		.cors(
				c ->{
					CorsConfigurationSource source = request ->{
						CorsConfiguration config = new CorsConfiguration();
						config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
								.map(String::trim)
								.filter(origin -> !origin.isEmpty())
								.toList());
						config.setAllowedHeaders(List.of("*"));
						config.setAllowedMethods(List.of("*"));
						config.setAllowCredentials(true);
						return config;
					};
					c.configurationSource(source);
				}
				)
		.authorizeHttpRequests(c ->{
			c.requestMatchers("/pheno/auth/**", "/pheno/phone/**").permitAll();
			c.requestMatchers("/pheno/user/**").authenticated();
			c.anyRequest().authenticated();
		});
		
		security.addFilterBefore((Filter) filter, UsernamePasswordAuthenticationFilter.class);
		
		return security.build();
		
	}

}
