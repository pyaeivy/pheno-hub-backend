package com.example.demo.dto;

import com.example.demo.entity.Currency;

import java.math.BigDecimal;

public class UserDto {

	public record RegisterRequest(String firstName, String lastName, String username, String email, String password,
			String phone, String address, Currency currency) {
	}

	public record RegisterResponse(String firstName, String lastName, String username, String email, String phone,
			String address, Currency currency, BigDecimal balance, BigDecimal income, BigDecimal expense,
			BigDecimal profit, BigDecimal loss, BigDecimal ownDevices, BigDecimal soldOutDevices

	) {
	}

	public record LoginRequest(String usernameOrEmail, String password) {
	}

	public record LoginResponse(String accessToken, RegisterResponse user) {
	}

	public record LoginResult(String accessToken, String refreshToken, RegisterResponse user) {
	}

	public record UpdateUserRequest(String firstName, String lastName,String username, String email, String phone, String address, Currency currency) {
	}

}
