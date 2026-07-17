package com.attackoncodes.worksync.security.dto.user;

import org.springframework.core.io.Resource;

public record UserExport(
		
		Resource resource,
		String filename,
		Integer filesize
		
) {

}
