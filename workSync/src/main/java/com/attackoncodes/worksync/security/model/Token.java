package com.attackoncodes.worksync.security.model;

import com.attackoncodes.worksync.global.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
public class Token extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "token", length = 1024, unique = true, nullable = false, updatable = false)
	private String token;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TokenType tokenType = TokenType.BEARER;

	@Column(name = "revoked", updatable = true)
	private boolean revoked;

	@Column(name = "expired", updatable = true)
	private boolean expired;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public boolean isValid() {
		return !this.revoked && !this.expired;
	}

	public void revoke() {
		this.revoked = true;
	}

	public void expire() {
		this.expired = true;
	}

}
