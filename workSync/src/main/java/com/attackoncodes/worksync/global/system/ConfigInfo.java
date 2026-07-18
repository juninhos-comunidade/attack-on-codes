package com.attackoncodes.worksync.global.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.attackoncodes.worksync.global.constraints.UserConstraints;

@Component
public class ConfigInfo extends UserConstraints {

	private ConfigInfo() {
		super();
	}
	
	@Value("${application.size.page:20}")
	private Integer applicationPageSizeDefault;
	
    public SystemInfo returnSystemBuilded() {
        return new SystemInfo(
            applicationPageSizeDefault,
            NICKNAME_MIN_LENGTH,
            NICKNAME_MAX_LENGTH,
            EMAIL_MIN_LENGTH,
            EMAIL_MAX_LENGTH,
            MINIMAL_PASSWORD_SIZE
        );
    }
}
