package com.attackoncodes.worksync.security.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tom.security.hash.security.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	@Query("""
			SELECT t FROM Token t
			WHERE t.user.id =:userId AND t.expired = false AND t.revoked = false
			""")
	List<Token> findAllValidTokensByUserId(UUID userId);

	@Query("SELECT t FROM Token t JOIN FETCH t.user u WHERE t.token = :token")
	Optional<Token> findByToken(@Param("token") String token);

}
