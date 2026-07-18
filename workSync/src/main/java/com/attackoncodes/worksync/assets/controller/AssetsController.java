package com.attackoncodes.worksync.assets.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.attackoncodes.worksync.assets.dto.AssetsResponse;
import com.attackoncodes.worksync.assets.service.AssetsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/assets")
@PreAuthorize("hasRole('FUNCIONARIO', 'ADMIN')")
@RequiredArgsConstructor
public class AssetsController {

	private final AssetsService service;

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public AssetsResponse findAssetById(@PathVariable(value = "id") UUID query) {
		return service.findAssetById(query);
	}

	/*
	
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AttachmentResponse> uploadObjectToNote(@RequestParam("name") String noteName,
			MultipartFile file, Principal connectedUser) {
		var response = service.uploadObjectToNote(noteName, file, connectedUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping(value = "/download", produces = "application/zip")
	public ResponseEntity<byte[]> downloadObjectFromNote(@RequestParam("name") String noteName,
			Principal connectedUser) {
		var response = service.downloadObjectFromNote(noteName, connectedUser);

		if (response == null) {
			return ResponseEntity.noContent().build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf("application/zip"));
		headers.setContentDispositionFormData("attachment", noteName + "_attachments.zip");

		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);
	}

	@DeleteMapping(value = "/delete")
	public ResponseEntity<Void> deleteObjectFromNote(@RequestParam("name") String noteName,
			@RequestParam("attachment") String attachmentName, Principal connectedUser) {
		service.deleteObjectFromNote(noteName, attachmentName, connectedUser);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	 */
}
