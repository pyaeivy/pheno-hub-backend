package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFountException;
import com.example.demo.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final AuthRepository repo;

    public UserDto.RegisterResponse findByPhone(String phone){
        User user = repo.findByPhone(phone).orElseThrow(() -> new ResourceNotFountException("User not found!"));
        return AuthService.toRegisterResponse(user);
    }

    public UserDto.RegisterResponse findByUsername(String username){
        User user = repo.findByUsername(username).orElseThrow(() -> new ResourceNotFountException("User not found!"));
        return AuthService.toRegisterResponse(user);
    }

    public UserDto.RegisterResponse findByEmail(String email){
        User user = repo.findByEmail(email).orElseThrow(() -> new ResourceNotFountException("User not found!"));
        return AuthService.toRegisterResponse(user);
    }

    public UserDto.RegisterResponse updateUserInfo(String username, UserDto.UpdateUserRequest request){
        User user = repo.findByUsername(username).orElseThrow(() -> new ResourceNotFountException("User not found!"));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(AuthService.validPhone(request.phone()));
        user.setAddress(request.address());
        user.setCurrency(request.currency());
        userDao.save(user);
        return AuthService.toRegisterResponse(user);
    }

}
