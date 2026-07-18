package com.attackoncodes.worksync.logic.bucket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "bucket")
public class AwsProperties {

	private String endpoint;
	private String accessKeyId;
	private String secretAccessKey;
	private String bucket;
	private String region;
	private String ddlAuto;

}