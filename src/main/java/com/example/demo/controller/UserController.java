package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pheno/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{phone}")
    public ResponseEntity<UserDto.RegisterResponse> getUserByPhone(@PathVariable String phone){
        UserDto.RegisterResponse response = service.findByPhone(phone);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto.RegisterResponse> getUserByUsername(@PathVariable String username){
        UserDto.RegisterResponse response = service.findByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto.RegisterResponse> getUserByEmail(@PathVariable String email){
        UserDto.RegisterResponse response = service.findByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/update/{username}")
    public ResponseEntity<UserDto.RegisterResponse> updateUserInfo(@PathVariable String username, @RequestBody UserDto.UpdateUserRequest request){
        UserDto.RegisterResponse response = service.updateUserInfo(username, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
