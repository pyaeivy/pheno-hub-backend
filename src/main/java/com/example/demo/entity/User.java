package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String firstName;
	private String lastName;
	@Column(unique = true, nullable = false)
	private String username;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(unique = true, nullable = false)
	private String password;
	@Column(unique = true, nullable = false)
	private String phone;
	@Column(unique = true, nullable = false)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Currency currency;

	private BigDecimal balance = BigDecimal.ZERO;
	private BigDecimal income = BigDecimal.ZERO;
	private BigDecimal expense = BigDecimal.ZERO;
	private BigDecimal profit = BigDecimal.ZERO;
	private BigDecimal loss = BigDecimal.ZERO;
	private BigDecimal ownDevices = BigDecimal.ZERO;
	private BigDecimal soldOutDevices = BigDecimal.ZERO;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	private PhoneModel phoneModel;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PhoneImage> phoneImages = new ArrayList<>();
}
