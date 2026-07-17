package com.attackoncodes.worksync.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tom.security.hash.global.system.ConfigInfo;
import com.tom.security.hash.global.system.SystemInfo;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/config")
public class ConfigController {

	private final ConfigInfo configInfo;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public SystemInfo showSystemInfo() {
		return configInfo.returnSystemBuilded();
	}
	
}
