package com.attackoncodes.worksync.security.dto.user;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"loginTime", "ipAddress"})
public record LoginHistoryResponse(
		
		ZonedDateTime loginTime,
		String ipAddress
		
		) {

}
