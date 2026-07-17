package com.attackoncodes.worksync.security.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import com.tom.security.hash.security.dto.authentication.AuthenticationResponse;
import com.tom.security.hash.security.dto.authentication.RegisterRequest;
import com.tom.security.hash.security.dto.user.AccountUpdateRequest;
import com.tom.security.hash.security.dto.user.DataExportation;
import com.tom.security.hash.security.dto.user.PageUserResponse;
import com.tom.security.hash.security.dto.user.UserResponse;
import com.tom.security.hash.security.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MappingUtils.class })
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "roles", ignore = true)
	User build(RegisterRequest request);

	AuthenticationResponse toResponse(String accessToken);

	@Mapping(source = "user", target = "lastLogin", qualifiedByName = "getLastLoginTime")
	UserResponse toResponse(User user);

	@Mapping(target = "id", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void update(@MappingTarget User user, AccountUpdateRequest request);

	@Mapping(source = "enabled", target = "accountEnabled")
	DataExportation dataExporation(User user);

	List<UserResponse> toResponseList(List<User> users);

	default PageUserResponse toResponse(Page<User> page) {
		if (page == null) {
			return null;
		}
		List<UserResponse> content = toResponseList(page.getContent());
		return new PageUserResponse(content, page.getNumber(), page.getSize(), page.getTotalPages());
	}

}
