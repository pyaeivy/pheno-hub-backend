package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "phone_spec")
public class PhoneSpecification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String network;
	private String technology;
	private String dimensions;
	private String weight;
	private boolean isIMEIPaid;
	private String displayType;
	private BigDecimal displaySize;
	private String displayResolution;
	private Integer displayRefreshRate;
	private String chipset;
	private String cpu;
	private String gpu;
	private String ram;
	private String storage;
	private String mainCamera;
	private String selfieCamera;
	private Integer batteryCapacity;
	private String charging;
	private String operatingSystem;
	private String bluetooth;
	private String wifi;
	private Boolean nfc;
	private String usb;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "phone_model_id", nullable = false, unique = true)
	private PhoneModel phoneModel;
}
