package com.example.demo.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.PhoneDto;
import com.example.demo.entity.ImageCategory;
import com.example.demo.service.PhoneService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pheno/phone")
@RequiredArgsConstructor
public class PhoneController {

	private final PhoneService phoneService;

	@PostMapping({"/add", "/add/{username}", "/addPhone", "/addPhone/{username}"})
	public ResponseEntity<PhoneDto.PhoneResponse> addPhone(
			@PathVariable(required = false) String username,
			@RequestBody PhoneDto.AddPhone dto,
			Principal principal
	) {
		String targetUsername = username;
		if (targetUsername == null || targetUsername.isBlank()) {
			targetUsername = dto.username();
		}
		if ((targetUsername == null || targetUsername.isBlank()) && principal != null) {
			targetUsername = principal.getName();
		}

		PhoneDto.PhoneResponse response = phoneService.addPhone(dto, targetUsername);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/addPhoneSpec/{modelId}")
	public ResponseEntity<PhoneDto.AddPhoneSpec> addPhoneSpec(
			@RequestBody PhoneDto.AddPhoneSpec spec,
			@PathVariable Long modelId){
		PhoneDto.AddPhoneSpec pSpec = phoneService.addPhoneSpec(spec, modelId);
		return ResponseEntity.status(HttpStatus.CREATED).body(pSpec);
	}


	@PostMapping(value = {"/image", "/image/{username}", "/createImage", "/createImage/{username}"},
			consumes = "multipart/form-data")
	public ResponseEntity<PhoneDto.ImageResponse> createImage(
			@PathVariable(required = false) String username,
			@RequestParam("file") MultipartFile file,
			@RequestParam("category") ImageCategory category,
			@RequestParam(required = false) Long ownerId,
			@RequestParam(required = false) String ownerUsername,
			@RequestParam(required = false) String imageOwner,
			Principal principal
	) throws java.io.IOException {
		String targetUsername = (username != null && !username.isBlank()) ? username : ownerUsername;
		if (targetUsername == null || targetUsername.isBlank()) {
			targetUsername = imageOwner;
		}
		if (ownerId == null && (targetUsername == null || targetUsername.isBlank()) && principal != null) {
			targetUsername = principal.getName();
		}

		PhoneDto.ImageResponse response = phoneService.createImage(file, category, ownerId, targetUsername);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping({"/username/{username}", "/phonelist/{username}", "/getphonelistbyusername/{username}"})
	public ResponseEntity<List<PhoneDto.PhoneResponse>> getPhoneListByUsername(@PathVariable String username) {
		List<PhoneDto.PhoneResponse> response = phoneService.getPhoneListByUsername(username);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping({"/selected/{username}", "/selected-phone/{username}", "/getselectedphonebyusername/{username}"})
	public ResponseEntity<PhoneDto.PhoneResponse> getSelectedPhoneByUsername(@PathVariable String username) {
		PhoneDto.PhoneResponse response = phoneService.getSelectedPhoneByUsername(username);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping({"/select/{username}/{modelId}", "/select-phone/{username}/{modelId}"})
	public ResponseEntity<PhoneDto.PhoneResponse> selectPhoneByUsername(
			@PathVariable String username,
			@PathVariable Long modelId
	) {
		PhoneDto.PhoneResponse response = phoneService.selectPhoneByUsername(username, modelId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
