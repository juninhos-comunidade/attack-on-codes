package com.attackoncodes.worksync.security.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tom.security.hash.security.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

	String ACTIVE_USER_CONDITION = "u.accountNonLocked = true AND u.enabled = true";

	@EntityGraph(attributePaths = { "roles" })
	@Query("SELECT u FROM User u WHERE " + ACTIVE_USER_CONDITION)
	Page<User> findAllActiveUsers(Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.id = :id AND " + ACTIVE_USER_CONDITION)
	Optional<User> findActiveUserById(UUID id);

	@Query("SELECT (COUNT(u) > 0) FROM User u WHERE u.email = :email AND u.id <> :id")
	boolean isEmailDuplicate(@Param("email") String email, @Param("id") UUID id);

	@Query("SELECT (COUNT(u) > 0) FROM User u WHERE u.nickname = :nickname AND u.id <> :id")
	boolean isNicknameDuplicate(@Param("nickname") String nickname, @Param("id") UUID id);

	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);
}
