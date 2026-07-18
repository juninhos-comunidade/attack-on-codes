package com.attackoncodes.worksync.security.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import com.attackoncodes.worksync.security.dto.authentication.AuthenticationResponse;
import com.attackoncodes.worksync.security.dto.authentication.RegisterRequest;
import com.attackoncodes.worksync.security.dto.user.AccountUpdateRequest;
import com.attackoncodes.worksync.security.dto.user.PageUserResponse;
import com.attackoncodes.worksync.security.dto.user.UserResponse;
import com.attackoncodes.worksync.security.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "roles", ignore = true)
	User build(RegisterRequest request);

	AuthenticationResponse toResponse(String accessToken);

	UserResponse toResponse(User user);

	@Mapping(target = "id", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void update(@MappingTarget User user, AccountUpdateRequest request);

	List<UserResponse> toResponseList(List<User> users);

	default PageUserResponse toResponse(Page<User> page) {
		if (page == null) {
			return null;
		}
		List<UserResponse> content = toResponseList(page.getContent());
		return new PageUserResponse(content, page.getNumber(), page.getSize(), page.getTotalPages());
	}

}
