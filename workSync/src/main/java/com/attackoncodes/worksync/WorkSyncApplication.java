package com.attackoncodes.worksync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.attackoncodes.worksync.config.CustomBanner;

@SpringBootApplication
public class WorkSyncApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WorkSyncApplication.class);
		app.setBanner(new CustomBanner());
		app.run(args);
	}

}
