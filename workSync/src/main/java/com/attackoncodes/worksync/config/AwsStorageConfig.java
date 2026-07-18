package com.attackoncodes.worksync.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.attackoncodes.worksync.logic.bucket.AwsProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class AwsStorageConfig {

    private final AwsProperties properties;

    @Bean
    S3Client s3Client() {
        return S3Client.builder()
        		.endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                //.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .credentialsProvider(StaticCredentialsProvider.create(
                		AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey())))
                .build();

        
        /*
        if(properties.isAccelerateEnabled()) {
        	enableAccelerateMode();
        }
        enableVersioning();
        
        */
    }
    
    /*
    
	private void enableAccelerateMode() {
        try {
			s3Client.putBucketAccelerateConfiguration(
			        PutBucketAccelerateConfigurationRequest.builder()
			                .bucket(properties.getBucket())
			                .accelerateConfiguration(
			                        AccelerateConfiguration.builder()
			                                .status(BucketAccelerateStatus.ENABLED)
			                                .build())
			                .build());
		} catch (S3Exception e) {
			ServiceLogger.error("Failed to enable aceleration: {}", e.awsErrorDetails().errorMessage());
		}
    }

    private void enableVersioning() {
        try {
			s3Client.putBucketVersioning(
			        PutBucketVersioningRequest.builder()
			                .bucket(properties.getBucket())
			                .versioningConfiguration(
			                        VersioningConfiguration.builder()
			                                .status(BucketVersioningStatus.ENABLED)
			                                .build())
			                .build());
		} catch (S3Exception e) {
			ServiceLogger.error("Failed to enable bucket versioning: {}", e.awsErrorDetails().errorMessage());
		}
    }
	
	*/
}
