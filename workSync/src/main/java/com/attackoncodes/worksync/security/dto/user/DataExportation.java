package com.attackoncodes.worksync.security.dto.user;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "nickname", "email", "role", "accountEnabled", "loginHistory" })
public record DataExportation(
		
	    UUID id,
	    String nickname,
	    String email,
	    String role,
	    Boolean accountEnabled,
	    List<LoginHistoryResponse> loginHistory
		
) {

}
