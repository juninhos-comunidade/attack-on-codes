package com.attackoncodes.worksync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.attackoncodes.worksync.global.system.SystemActionContext;
import com.attackoncodes.worksync.logic.bucket.AwsProperties;
import com.attackoncodes.worksync.security.model.Role;
import com.attackoncodes.worksync.security.model.User;
import com.attackoncodes.worksync.security.repository.UserRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemStart implements CommandLineRunner {

	@Value("${settings.security.user}")
	private String username;

	@Value("${settings.security.email}")
	private String email;

	@Value("${settings.security.password}")
	private String password;

	private final SystemActionContext systemAction;

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;

	private final AwsProperties properties;
	private final S3Client s3Client;

	@Getter
	private boolean s3Connected = false;

	@Override
	public void run(String... args) throws Exception {
		generateAdminUser();
		verifyS3Connection();
	}

	@EventListener(ContextRefreshedEvent.class)
	public void generateAdminUser() {
		systemAction.runAsSystem(() -> {
			if (!repository.existsByNickname(username)) {
				User user = new User();
				user.setNickname(username);
				user.setEmail(email);
				user.setPassword(passwordEncoder.encode(password));
				user.setRoles(Role.ADMIN);
				try {
					repository.save(user);
					log.info(">>>> Default ADMIN user created: {}", username);
				} catch (DataIntegrityViolationException e) {
					log.warn(">>>> Default ADMIN user '{}' was created by another instance concurrently. Skipping.",
							username);
				}
			} else {
				log.info(">>>> Default ADMIN user '{}' already exists. Skipping creation.", username);
			}
			return null;
		});
	}

	@EventListener(ContextRefreshedEvent.class)
	public void verifyS3Connection() {
		try {
			s3Client.getBucketLocation(b -> b.bucket(properties.getBucket()));
			log.info("Connection successful on bucket: " + properties.getBucket());
			s3Connected = true;
		} catch (Exception e) {
			log.error("S3 connection failed: " + e.getMessage());
			s3Connected = false;
		}
	}
}
