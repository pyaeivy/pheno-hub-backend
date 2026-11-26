package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "phone_image")
public class PhoneImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String image;
	private LocalDateTime createdAt;
	@Enumerated(EnumType.STRING)
	private ImageCategory imageCategory;
	private Long imageSize;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "phone_model_id")
	private PhoneModel phoneModel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public PhoneImage(String image, PhoneModel phoneModel, User user) {
		this.image = image;
		this.phoneModel = phoneModel;
		this.user = user;
		this.createdAt = LocalDateTime.now();
	}
}

