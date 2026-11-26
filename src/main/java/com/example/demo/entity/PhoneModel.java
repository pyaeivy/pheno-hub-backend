package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "phone_model", uniqueConstraints = {
		@UniqueConstraint(name = "uk_phone_model", columnNames = { "brand_id", "name" }) }

)
public class PhoneModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String slug;
	private LocalDate releaseDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "phoneModel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PhoneImage> phoneImage = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	private PhoneBrand phoneBrand;

	@OneToMany(mappedBy = "phoneModel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PhoneImage> phoneImages = new ArrayList<>();
}
