package com.example.demo.dto;

import com.example.demo.entity.ImageCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PhoneDto {

	public record AddPhone(
		String brandName,
		String modelName,
		LocalDate releaseDate,
		String image,
		String username
	) {
		public AddPhone(String brandName, String modelName, LocalDate releaseDate, String image) {
			this(brandName, modelName, releaseDate, image, null);
		}
	}
	
	public record AddPhoneSpec(
			 String network,
			 String technology,
			 String dimensions,
			 String weight,
			 boolean isIMEIPaid,
			 String displayType,
			 BigDecimal displaySize,
			 String displayResolution,
			 Integer displayRefreshRate,
			 String chipset,
			 String cpu,
			 String gpu,
			 String ram,
			 String storage,
			 String mainCamera,
			 String selfieCamera,
			 Integer batteryCapacity,
			 String charging,
			 String operatingSystem,
			 String bluetooth,
			 String wifi,
			 Boolean nfc,
			 String usb
	){}

	public record PhoneResponse(
		Long phoneModelId,
		String brandName,
		String modelName,
		String slug,
		LocalDate releaseDate,
		String image,
		String username,
		LocalDateTime createdAt,
		boolean isSelected
	) {}

	public  record ImageResponse(
			String name,
			LocalDateTime createdAt,
			ImageCategory imageCategory,
			Long imageSize,
			String phoneName,
			String username
	){}

	public record SelectPhoneRequest(
		String username,
		Long phoneModelId
	) {}
}
