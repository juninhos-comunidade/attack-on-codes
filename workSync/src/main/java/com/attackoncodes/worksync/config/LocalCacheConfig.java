package com.attackoncodes.worksync.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableCaching
@Profile("!cluster")
public class LocalCacheConfig {

	@Bean
	HazelcastInstance hazelcastInstance() {
		return Hazelcast.newHazelcastInstance();
	}

}
