package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.example.demo.entity.*;
import com.example.demo.exception.CredentialValidationException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.implementation.LocalStorageProvider;
import com.example.demo.repository.AuthRepository;
import com.example.demo.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.PhoneBrandDao;
import com.example.demo.dao.PhoneImageDao;
import com.example.demo.dao.PhoneModelDao;
import com.example.demo.dao.PhoneSpecificationDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.PhoneDto;
import com.example.demo.exception.EmptyValueException;
import com.example.demo.exception.ResourceNotFountException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class PhoneService {

	private final PhoneModelDao phoneModelDao;
	private final PhoneBrandDao phoneBrandDao;
	private final PhoneSpecificationDao phoneSpecificationDao;
	private final AuthRepository authRepo;
	private final PhoneRepository phoneRepo;
	private final PhoneImageDao phoneImageDao;
	private final UserDao userDao;
	private final LocalStorageProvider provider;

	@Value("${app.storage.provider:local}")
	private String storageProviderName;

	@Transactional
	public PhoneDto.PhoneResponse addPhone(PhoneDto.AddPhone dto, String username) {
		String targetUsername = (username != null && !username.isBlank()) ? username : dto.username();
		if (targetUsername == null || targetUsername.isBlank()) {
			throw new EmptyValueException("Username must be provided to add a phone!");
		}

		if (dto.brandName() == null || dto.brandName().isBlank()) {
			throw new EmptyValueException("Brand name must not be empty!");
		}else if(phoneRepo.isExistByBrandName(dto.brandName())){
			throw new ResourceAlreadyExistsException("Phone brand is already exists!");
		}

		if (dto.modelName() == null || dto.modelName().isBlank()) {
			throw new EmptyValueException("Model name must not be empty!");
		}

		User user = authRepo.findByUsername(targetUsername.trim())
				.orElseThrow(() -> new ResourceNotFountException("User not found: " + targetUsername));


		String brandName = dto.brandName().trim();
		PhoneBrand phoneBrand = phoneRepo.getPhoneBrandByName(brandName)
				.orElseGet(() -> {
					PhoneBrand brand = new PhoneBrand();
					brand.setName(brandName);
					brand.setSlug(toSlug(brandName));
					brand.setCratedAt(LocalDateTime.now());
					return phoneBrandDao.save(brand);
				});

		String modelName = dto.modelName().trim();
		PhoneModel phoneModel = phoneRepo.getPhoneModelByName(modelName)
				.orElseGet(() -> {
					PhoneModel model = new PhoneModel();
					model.setName(modelName);
					model.setPhoneBrand(phoneBrand);
					model.setReleaseDate(dto.releaseDate());
					model.setSlug(toSlug(phoneBrand.getName() + " " + modelName));
					model.setCreatedAt(LocalDateTime.now());
					model.setUpdatedAt(LocalDateTime.now());
					return phoneModelDao.save(model);
				});

		if (user.getPhoneModel() == null) {
			user.setPhoneModel(phoneModel);
		}

		BigDecimal currentOwn = user.getOwnDevices() != null ? user.getOwnDevices() : BigDecimal.ZERO;
		user.setOwnDevices(currentOwn.add(BigDecimal.ONE));
		userDao.save(user);

		boolean isSelected = user.getPhoneModel() != null && user.getPhoneModel().getId().equals(phoneModel.getId());
		return toPhoneResponse(phoneModel, null, user.getUsername(), phoneModel.getCreatedAt(), isSelected);
	}

	public PhoneDto.AddPhoneSpec addPhoneSpec(PhoneDto.AddPhoneSpec spec, Long modelId){
		PhoneModel phoneModel = phoneRepo.getPhoneById(modelId)
				.orElseThrow(() -> new ResourceNotFountException("Phone model not found!"));

		PhoneSpecification phoneSpecification = setPhoneSpec(spec, phoneModel);

		phoneSpecificationDao.save(phoneSpecification);
		return spec;
	}

	private static @NonNull PhoneSpecification setPhoneSpec(PhoneDto.AddPhoneSpec spec, PhoneModel phoneModel) {
		PhoneSpecification phoneSpecification = new PhoneSpecification();
		phoneSpecification.setNetwork(spec.network());
		phoneSpecification.setTechnology(spec.technology());
		phoneSpecification.setDimensions(spec.dimensions());
		phoneSpecification.setWeight(spec.weight());
		phoneSpecification.setIMEIPaid(spec.isIMEIPaid());
		phoneSpecification.setDisplayType(spec.displayType());
		phoneSpecification.setDisplaySize(spec.displaySize());
		phoneSpecification.setDisplayResolution(spec.displayResolution());
		phoneSpecification.setDisplayRefreshRate(spec.displayRefreshRate());
		phoneSpecification.setChipset(spec.chipset());
		phoneSpecification.setCpu(spec.cpu());
		phoneSpecification.setGpu(spec.gpu());
		phoneSpecification.setRam(spec.ram());
		phoneSpecification.setStorage(spec.storage());
		phoneSpecification.setMainCamera(spec.mainCamera());
		phoneSpecification.setSelfieCamera(spec.selfieCamera());
		phoneSpecification.setBatteryCapacity(spec.batteryCapacity());
		phoneSpecification.setCharging(spec.charging());
		phoneSpecification.setOperatingSystem(spec.operatingSystem());
		phoneSpecification.setBluetooth(spec.bluetooth());
		phoneSpecification.setWifi(spec.wifi());
		phoneSpecification.setNfc(spec.nfc());
		phoneSpecification.setUsb(spec.usb());
		phoneSpecification.setPhoneModel(phoneModel);
		return phoneSpecification;
	}

	@Transactional
	public PhoneDto.ImageResponse createImage(
			MultipartFile file,
			ImageCategory category,
			Long ownerId,
			String ownerUsername
	) throws IOException {
		validateImage(file);
		if (category == null) {
			throw new EmptyValueException("Image category must be provided!");
		}
		if ((ownerId == null) == (ownerUsername == null || ownerUsername.isBlank())) {
			throw new EmptyValueException("Provide exactly one image owner ID or username!");
		}

		User user = ownerId != null
				? userDao.findById(ownerId).orElseThrow(() -> new ResourceNotFountException("User not found: " + ownerId))
				: authRepo.findByUsername(ownerUsername.trim())
						.orElseThrow(() -> new ResourceNotFountException("User not found: " + ownerUsername));
		boolean isProfileImage = category == ImageCategory.PROFILE;
		PhoneModel phoneModel = isProfileImage ? null : user.getPhoneModel();
		if (!isProfileImage && phoneModel == null) {
			throw new ResourceNotFountException("Select a phone before uploading an image!");
		}

		String extension = getExtension(file.getOriginalFilename());
		String storedName = UUID.randomUUID() + "." + extension;
		String storageKey = category.name().toLowerCase(Locale.ROOT) + "/" + storedName;
		String storedImage = provider.upload(storageKey, file);

		try {
			PhoneImage image = new PhoneImage();
			image.setImage(storedImage);
			image.setCreatedAt(LocalDateTime.now());
			image.setImageCategory(category);
			image.setImageSize(file.getSize());
			image.setPhoneModel(phoneModel);
			image.setUser(user);

			PhoneImage savedImage = phoneImageDao.save(image);
			return new PhoneDto.ImageResponse(
					storedName,
					savedImage.getCreatedAt(),
					savedImage.getImageCategory(),
					savedImage.getImageSize(),
					phoneModel != null ? phoneModel.getName() : null,
					user.getUsername()
			);
		} catch (RuntimeException e) {
			try {
				provider.delete(storageKey);
			} catch (IOException cleanupFailure) {
				e.addSuppressed(cleanupFailure);
			}
			throw e;
		}
	}

	@Transactional(readOnly = true)
	public List<PhoneDto.PhoneResponse> getPhoneListByUsername(String username) {
		if (username == null || username.isBlank()) {
			throw new EmptyValueException("Username must not be empty!");
		}

		User user = authRepo.findByUsername(username.trim())
				.orElseThrow(() -> new ResourceNotFountException("User not found: " + username));

		List<PhoneImage> images = phoneImageDao.findByUserUsernameOrderByIdDesc(user.getUsername()).stream()
				.filter(image -> image.getPhoneModel() != null)
				.toList();
		Long selectedModelId = user.getPhoneModel() != null ? user.getPhoneModel().getId() : null;

		if (images.isEmpty() && user.getPhoneModel() != null) {
			PhoneModel selected = user.getPhoneModel();
			return List.of(new PhoneDto.PhoneResponse(
					selected.getId(),
					selected.getPhoneBrand() != null ? selected.getPhoneBrand().getName() : null,
					selected.getName(),
					selected.getSlug(),
					selected.getReleaseDate(),
					"",
					user.getUsername(),
					selected.getCreatedAt(),
					true
			));
		}

		return images.stream().map(img -> {
			boolean isSelected = selectedModelId != null && selectedModelId.equals(img.getPhoneModel().getId());
			return toPhoneResponse(img, isSelected);
		}).toList();
	}

	@Transactional
	public PhoneDto.PhoneResponse getSelectedPhoneByUsername(String username) {
		if (username == null || username.isBlank()) {
			throw new EmptyValueException("Username must not be empty!");
		}

		User user = authRepo.findByUsername(username.trim())
				.orElseThrow(() -> new ResourceNotFountException("User not found: " + username));

		PhoneModel selectedModel = user.getPhoneModel();

		if (selectedModel == null) {
			List<PhoneImage> images = phoneImageDao.findByUserUsernameOrderByIdDesc(user.getUsername());
			if (!images.isEmpty()) {
				selectedModel = images.getFirst().getPhoneModel();
				user.setPhoneModel(selectedModel);
				userDao.save(user);
			} else {
				throw new ResourceNotFountException("No selected phone found for user: " + username);
			}
		}

		Optional<PhoneImage> phoneImageOpt = phoneImageDao.findFirstByUserUsernameAndPhoneModelIdOrderByIdDesc(
				user.getUsername(),
				selectedModel.getId()
		);

		Long imageId = phoneImageOpt.map(PhoneImage::getId).orElse(null);
		String image = phoneImageOpt.map(PhoneImage::getImage).orElse(null);
		LocalDateTime createdAt = phoneImageOpt.map(PhoneImage::getCreatedAt).orElse(selectedModel.getCreatedAt());

		return new PhoneDto.PhoneResponse(
				selectedModel.getId(),
				selectedModel.getPhoneBrand() != null ? selectedModel.getPhoneBrand().getName() : null,
				selectedModel.getName(),
				selectedModel.getSlug(),
				selectedModel.getReleaseDate(),
				image,
				user.getUsername(),
				createdAt,
				true
		);
	}

	@Transactional
	public PhoneDto.PhoneResponse selectPhoneByUsername(String username, Long phoneModelId) {
		if (username == null || username.isBlank()) {
			throw new EmptyValueException("Username must not be empty!");
		}

		User user = authRepo.findByUsername(username.trim())
				.orElseThrow(() -> new ResourceNotFountException("User not found: " + username));

		PhoneModel phoneModel = phoneModelDao.findById(phoneModelId)
				.orElseThrow(() -> new ResourceNotFountException("Phone model not found: " + phoneModelId));

		user.setPhoneModel(phoneModel);
		userDao.save(user);

		Optional<PhoneImage> phoneImageOpt = phoneImageDao.findFirstByUserUsernameAndPhoneModelIdOrderByIdDesc(
				user.getUsername(),
				phoneModel.getId()
		);

		Long imageId = phoneImageOpt.map(PhoneImage::getId).orElse(null);
		String image = phoneImageOpt.map(PhoneImage::getImage).orElse(null);
		LocalDateTime createdAt = phoneImageOpt.map(PhoneImage::getCreatedAt).orElse(phoneModel.getCreatedAt());

		return new PhoneDto.PhoneResponse(
				phoneModel.getId(),
				phoneModel.getPhoneBrand() != null ? phoneModel.getPhoneBrand().getName() : null,
				phoneModel.getName(),
				phoneModel.getSlug(),
				phoneModel.getReleaseDate(),
				image,
				user.getUsername(),
				createdAt,
				true
		);
	}
	public PhoneDto.PhoneResponse toPhoneResponse(PhoneImage phoneImage, boolean isSelected) {
		PhoneModel model = phoneImage.getPhoneModel();
		String brandName = (model != null && model.getPhoneBrand() != null) ? model.getPhoneBrand().getName() : null;
		String modelName = model != null ? model.getName() : null;
		String slug = model != null ? model.getSlug() : null;
		var releaseDate = model != null ? model.getReleaseDate() : null;
		String username = phoneImage.getUser() != null ? phoneImage.getUser().getUsername() : null;

		return new PhoneDto.PhoneResponse(
				model != null ? model.getId() : null,
				brandName,
				modelName,
				slug,
				releaseDate,
				phoneImage.getImage(),
				username,
				phoneImage.getCreatedAt(),
				isSelected
		);
	}

	public PhoneDto.PhoneResponse toPhoneResponse(PhoneModel phoneModel, String image, String username, LocalDateTime createdAt, boolean isSelected) {
		String brandName = (phoneModel != null && phoneModel.getPhoneBrand() != null) ? phoneModel.getPhoneBrand().getName() : null;
		String modelName = phoneModel != null ? phoneModel.getName() : null;
		String slug = phoneModel != null ? phoneModel.getSlug() : null;
		var releaseDate = phoneModel != null ? phoneModel.getReleaseDate() : null;

		return new PhoneDto.PhoneResponse(
				phoneModel != null ? phoneModel.getId() : null,
				brandName,
				modelName,
				slug,
				releaseDate,
				image,
				username,
				createdAt,
				isSelected
		);
	}

	private String toSlug(String input) {
		if (input == null) return null;
		return input.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
	}

	private void validateImage(MultipartFile file) {

		Set<String> allowType = Set.of(
				"image/jpeg",
				"image/jpg",
				"image/gif"
		);
		if (file == null || file.isEmpty()){
			throw new EmptyValueException("Image is required!");
		}
		if (file.getSize() > 5 * 1024 * 1024){
			throw new CredentialValidationException("Maximum image size is 5 MB !");
		}
		if (!allowType.contains(file.getContentType())){
			throw new CredentialValidationException("Invalid image format!");
		}

		try(InputStream input = file.getInputStream()){
			if (ImageIO.read(input) == null){
				throw new EmptyValueException("Invalid image file!");
			}
		}catch (IOException e){
			throw new IllegalArgumentException("Unable to read image", e);
		}
	}

	private String getExtension(String fileName){
		if (fileName == null || !fileName.contains(".")){
			throw new IllegalArgumentException("Invalid filename!");
		}
		return fileName.substring(fileName.lastIndexOf('.')+ 1).toLowerCase(Locale.ROOT);
	}


}
