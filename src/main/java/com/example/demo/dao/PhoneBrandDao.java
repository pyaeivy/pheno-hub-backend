package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.PhoneBrand;

public interface PhoneBrandDao extends JpaRepository<PhoneBrand, Long> {
}
