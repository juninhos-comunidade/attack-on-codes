package com.attackoncodes.worksync.security.mapper;

import java.time.ZonedDateTime;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import com.tom.security.hash.security.model.User;
import com.tom.security.hash.security.repository.LoginHistoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MappingUtils {

	private final LoginHistoryRepository loginHistoryRepository;

	@Named("getLastLoginTime")
	public ZonedDateTime getLastLoginTime(User user) {
		if (user == null || user.getId() == null) {
			return null;
		}
		return loginHistoryRepository.findLatestByUserId(user.getId()).map(loginHistory -> loginHistory.getLoginTime())
				.orElse(null);
	}

}
