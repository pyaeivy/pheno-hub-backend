package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.CredentialValidationException;
import com.example.demo.exception.EmptyValueException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFountException;
import com.example.demo.repository.AuthRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.PasswordStrength;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.UserDto;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userDao;
    private final AuthRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public UserDto.RegisterResponse register(UserDto.RegisterRequest request) {
        validateUserExists(request.username(), request.email());

        User user = createUserFromRequest(request);
        userDao.save(user);

        return toRegisterResponse(user);
    }

    public UserDto.LoginResult login(UserDto.LoginRequest request){
        Optional<User> user = repo.findByUsernameOrEmail(request.usernameOrEmail());
        if(user.isEmpty()){
            throw new ResourceNotFountException("User not found");
        }
        if(!passwordEncoder.matches(request.password(), user.get().getPassword())){
            throw new ResourceNotFountException("Invalid password");
        }
        List<String> roles = user.get().getRoles().stream().map(Role::getName).toList();
        String accessToken = jwtUtil.generateAccessToken(request.usernameOrEmail(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(request.usernameOrEmail(), roles);

        return new UserDto.LoginResult(accessToken, refreshToken, toRegisterResponse(user.get()));
    }

    public static String validPhone(String phone){
        if (phone == null || phone.isEmpty()) {
            throw new EmptyValueException("Empty phone number, fill valid phone number!");
        }

        phone = phone.trim();

        if (phone.matches("^09\\d{9}$")){
            return phone;
        }
        if (phone.matches("^\\+?959\\d{9}$")) {
            return 0 + phone.replace("+", "").substring(2);
        }

        throw new CredentialValidationException("Invalid phone number, fill valid phone number!");

    }

    public String validPassword(String password){
        PasswordStrength strength = passwordStrength(password);

        switch (strength){
            case INVALID -> throw new CredentialValidationException("Empty password, fill valid strong password!");
            case WEAK -> throw new CredentialValidationException("Password must be at least 8 characters long!");
            case MEDIUM -> throw new CredentialValidationException("Password must contain uppercase, lowercase, number and special character!");
            case STRONG -> {
                return password;
            }
        }

        throw new CredentialValidationException("Password validation failed!");

    }

    public PasswordStrength passwordStrength(String password){
        if (password.isEmpty()){
            return PasswordStrength.INVALID;
        }
        if(password.length() < 8){
            return PasswordStrength.WEAK;
        }
            boolean hasUpperCase = password.matches(".*[A-Z].*");
            boolean hasLowerCase = password.matches(".*[a-z].*");
            boolean hasNumber = password.matches(".*[0-9].*");
            boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");

            int score = 0;
            if (hasUpperCase) score++;
            if (hasLowerCase) score++;
            if (hasNumber) score++;
            if (hasSpecialChar) score++;

            if (score >= 4){
                return PasswordStrength.STRONG;
            }
            return PasswordStrength.MEDIUM;

    }


    public void validateUserExists(String username, String email) {
        if (repo.findByUsername(username).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }
        if (repo.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
    }

    private User createUserFromRequest(UserDto.RegisterRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(validPassword(request.password())));
        user.setPhone(validPhone(request.phone()));
        user.setAddress(request.address());
        user.setCurrency(request.currency());
        return user;
    }

    public static UserDto.RegisterResponse toRegisterResponse(User user) {
        return new UserDto.RegisterResponse(
                user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(),
                user.getPhone(), user.getAddress(), user.getCurrency(), user.getBalance(), user.getIncome(),
                user.getExpense(), user.getProfit(), user.getLoss(), user.getOwnDevices(), user.getSoldOutDevices());
    }
}
