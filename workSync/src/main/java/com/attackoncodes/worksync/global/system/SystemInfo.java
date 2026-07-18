package com.attackoncodes.worksync.global.system;

public record SystemInfo(
		
		Integer applicationPageSizeDefault,
		Integer nicknameMinLength,
		Integer nicknameMaxLength,
		Integer emailMinLength,
		Integer emailMaxLength,
		Integer minimalPasswordSize
		
) {

}
