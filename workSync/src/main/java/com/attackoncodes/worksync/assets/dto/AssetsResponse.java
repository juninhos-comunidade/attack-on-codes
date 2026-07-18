package com.attackoncodes.worksync.assets.dto;

import java.time.LocalDateTime;

public record AssetsResponse(
		
		String name,
	    String objectUrl,
		String contentType,
		Long size,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
		
		) {

}
