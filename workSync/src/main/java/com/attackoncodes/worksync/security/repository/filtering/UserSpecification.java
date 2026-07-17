package com.attackoncodes.worksync.security.repository.filtering;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.tom.security.hash.security.enums.Role;
import com.tom.security.hash.security.model.User;

import jakarta.persistence.criteria.Predicate;

@Component
public class UserSpecification {

	public static Specification<User> findByCriteria(String nickname, String email, Role roles) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (nickname != null && !nickname.trim().isEmpty()) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nickname")),
						"%" + nickname.toLowerCase() + "%"));
			}

			if (email != null && !email.trim().isEmpty()) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
						"%" + email.toLowerCase() + "%"));
			}

			if (roles != null) {
				predicates.add(criteriaBuilder.equal(root.get("role"), roles));
			}

			if (predicates.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
