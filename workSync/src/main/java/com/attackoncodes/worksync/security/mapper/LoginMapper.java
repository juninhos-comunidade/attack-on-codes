package com.attackoncodes.worksync.security.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import com.tom.security.hash.security.dto.user.LoginHistoryResponse;
import com.tom.security.hash.security.dto.user.PageLoginHistoryResponse;
import com.tom.security.hash.security.model.LoginHistory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginMapper {

	List<LoginHistoryResponse> toResponseList(List<LoginHistory> history);

	default PageLoginHistoryResponse toResponse(Page<LoginHistory> page) {
		if (page == null) {
			return null;
		}

		List<LoginHistoryResponse> content = toResponseList(page.getContent());
		return new PageLoginHistoryResponse(content, page.getNumber(), page.getSize(), page.getTotalPages());
	}

}
