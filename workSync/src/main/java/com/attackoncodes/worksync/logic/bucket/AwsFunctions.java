package com.attackoncodes.worksync.logic.bucket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.attackoncodes.worksync.exception.server.DataTransferenceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsFunctions {

	private final AwsProperties awsProperties;
	private final S3Client s3Client;
	
	public String buildS3Url(String key) {
	    String s3Url = s3Client
	    		.utilities()
	    		.getUrl(builder -> builder.bucket(awsProperties.getBucket()).key(key))
	    		.toExternalForm();
	    return s3Url;
	}
	
	public void putObject(String key, MultipartFile file) {
		try {
		    s3Client
		    		.putObject(PutObjectRequest.builder()
			        .bucket(awsProperties.getBucket())
			        .key(key)
			        .contentType(file.getContentType())
			        .build(),
			        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
		        );
		    log.info("Successfully uploaded");
		} catch (IOException | S3Exception | SdkClientException e) {
			throw new DataTransferenceException("Error during S3 file transfer", e);
	    }
	}
	
	public byte[] objectAsBytes(String key) {
	    ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client
	            .getObject(GetObjectRequest.builder()
	                    .bucket(awsProperties.getBucket())
	                    .key(key)
	                    .build());

	    try (s3ObjectStream) { 
	        return s3ObjectStream.readAllBytes();
	    } catch (IOException | S3Exception | SdkClientException e) {
	        throw new DataTransferenceException("Error reading file from storage", e);
	    }
	}
	
	public void deleteObject(String key) {
        s3Client
			.deleteObject(DeleteObjectRequest.builder()
			.bucket(awsProperties.getBucket())
			.key(key)
			.build());
	}
	
	public String renameObject(String key, String newName) {
	    String originalKey = key;
	    String extension = originalKey.contains(".") ? originalKey.substring(originalKey.lastIndexOf(".")) : "";
	    
	    String newKey = newName + extension;
	    
	    s3Client.copyObject(builder -> builder
	            .sourceBucket(awsProperties.getBucket())
	            .sourceKey(key)
	            .destinationBucket(awsProperties.getBucket())
	            .destinationKey(newKey)
        );
	    
	    s3Client.deleteObject(builder -> builder
	            .bucket(awsProperties.getBucket())
	            .key(key)
        );
	    
	    return newKey;
	}
	
	public List<String> listAllObjectKeys() {
	    List<String> keys = new ArrayList<>();
	    try {
	        ListObjectsV2Request request = ListObjectsV2Request.builder()
	                .bucket(awsProperties.getBucket())
	                .build();
	        
	        ListObjectsV2Iterable responses = s3Client.listObjectsV2Paginator(request);

	        for (ListObjectsV2Response page : responses) {
	            page.contents().forEach(object -> keys.add(object.key()));
	        }
	    } catch (S3Exception e) {
	        log.error("Couldn't list object keys.", e);
	    }
	    return keys;
	}
}
