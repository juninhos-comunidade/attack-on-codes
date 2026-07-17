package com.attackoncodes.worksync.security.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tom.security.hash.security.model.LoginHistory;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {

	@Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId ORDER BY lh.loginTime DESC")
	Page<LoginHistory> findAllByUser(UUID userId, Pageable pageable);

	@Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId ORDER BY lh.loginTime DESC LIMIT 1")
	Optional<LoginHistory> findLatestByUserId(UUID userId);

}
