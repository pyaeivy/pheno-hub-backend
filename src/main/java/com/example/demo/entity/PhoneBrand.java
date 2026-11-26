package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "phone_brand")
public class PhoneBrand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String name;
	@Column(unique = true)
	private String slug;
	
	private String logUrl;
	private LocalDateTime cratedAt;

	@OneToMany(mappedBy = "phoneBrand")
	private List<PhoneModel> phoneModels = new ArrayList<>();
	
}
