package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String username);

}
