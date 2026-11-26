package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.PhoneImage;
import com.example.demo.entity.PhoneModel;
import com.example.demo.entity.User;

public interface PhoneImageDao extends JpaRepository<PhoneImage, Long> {

	List<PhoneImage> findByUser(User user);

	List<PhoneImage> findByUserUsername(String username);

	List<PhoneImage> findByUserUsernameOrderByIdDesc(String username);

	List<PhoneImage> findByPhoneModel(PhoneModel phoneModel);

	Optional<PhoneImage> findFirstByUserAndPhoneModelOrderByIdDesc(User user, PhoneModel phoneModel);

	Optional<PhoneImage> findFirstByUserUsernameAndPhoneModelIdOrderByIdDesc(String username, Long phoneModelId);

	@Query("SELECT DISTINCT pi.phoneModel FROM PhoneImage pi WHERE pi.user.username = :username")
	List<PhoneModel> findPhoneModelsByUsername(@Param("username") String username);
}

