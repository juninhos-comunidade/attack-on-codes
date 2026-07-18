package com.attackoncodes.worksync.security.repository.filtering;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class UserSortParameter {

	public Sort selectUserSort(UserSortOption sort) {
		if (sort == null) {
			return Sort.by("role").ascending();
		}

		return switch (sort) {
		case MOST_RECENT -> Sort.by("createdAt").descending();
		case NICKNAME -> Sort.by("nickname").ascending();
		case EMAIL -> Sort.by("email").ascending();
		case ROLE -> Sort.by("role").ascending();
		};
	}
}