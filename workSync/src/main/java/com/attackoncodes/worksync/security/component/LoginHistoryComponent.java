package com.attackoncodes.worksync.security.component;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import com.tom.security.hash.security.model.LoginHistory;
import com.tom.security.hash.security.model.User;
import com.tom.security.hash.security.repository.LoginHistoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginHistoryComponent {

	private final LoginHistoryRepository repository;
	
	// Mapstruct is unable to do this
	public void storeLoginHistory(User user, String ip) {
		ZonedDateTime time = ZonedDateTime.now();
		
		var login = LoginHistory.builder()
				.user(user)
				.loginTime(time)
				.ipAddress(ip)
				.build();
		
		// var login = mapper.build(user, time, ip);
		repository.save(login);
	}
	
}
