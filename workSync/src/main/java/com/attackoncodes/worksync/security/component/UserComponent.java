package com.attackoncodes.worksync.security.component;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.attackoncodes.worksync.exception.sql.DataViolationException;
import com.attackoncodes.worksync.exception.sql.NotFoundException;
import com.attackoncodes.worksync.security.model.User;
import com.attackoncodes.worksync.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserComponent {

	private final UserRepository repository;

	public User findByActiveUser(UUID userId) {
		return repository.findActiveUserById(userId)
				.orElseThrow(() -> new NotFoundException(String.format("The user id: %s was not found", userId)));
	}

	public User findById(UUID userId) {
		return repository.findById(userId)
				.orElseThrow(() -> new NotFoundException(String.format("The user id: %s was not found", userId)));
	}

	public User findByEmail(String email) {
		return repository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException(String.format("The user email: %s was not found", email)));
	}
	
	public void ensureNicknameAndEmailAreUnique(String nickname, String email) {
		if (repository.existsByNickname(nickname)) {
			throw new DataViolationException("Nickname is already taken: " + nickname);
		}

		if (repository.existsByEmail(email)) {
			throw new DataViolationException("Email is already in use: " + email);
		}
	}
	
	public void checkIfEmailAlreadyUsed(User currentUser, String newEmail) {
		if (repository.isEmailDuplicate(newEmail, currentUser.getId())) {
			throw new DataViolationException("Email is already in use by another account: " + newEmail);
		}
	}

	public void checkIfNicknameAlreadyUsed(User currentUser, String newUsername) {
		if (repository.isNicknameDuplicate(newUsername, currentUser.getId())) {
			throw new DataViolationException("Nickname is already taken by another account: " + newUsername);
		}
	}
	
}
