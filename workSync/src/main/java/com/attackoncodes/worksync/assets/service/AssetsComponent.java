package com.attackoncodes.worksync.assets.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.attackoncodes.worksync.assets.model.Assets;
import com.attackoncodes.worksync.assets.repository.AssetsRepository;
import com.attackoncodes.worksync.exception.sql.DataViolationException;
import com.attackoncodes.worksync.exception.sql.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssetsComponent {

	private final AssetsRepository repository;

	public Assets findById(UUID query) {
		return repository.findById(query)
				.orElseThrow(() -> new NotFoundException("Asset with the id was not found: " + query));
	}

	public Assets ensureObjectExistAndGet(String name) {
		return repository.findByName(name)
				.orElseThrow(() -> new NotFoundException("Asset with the name was not found: " + name));
	}

	public void checkIfObjectAlreadyExists(String name) {
		if (repository.existsByName(name)) {
			throw new DataViolationException("Asset already exists: " + name);
		}
	}

	public void checkIfAttachmentIsSame(Assets currentName, String newName) {
		repository.findByName(newName).ifPresent(existent -> {
			if (!existent.getId().equals(currentName.getId())) {
				throw new DataViolationException("Asset with name: " + newName + " is already in use.");
			}
		});
	}

}
