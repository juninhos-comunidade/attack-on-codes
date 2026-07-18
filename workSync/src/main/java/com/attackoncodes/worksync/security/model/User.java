package com.attackoncodes.worksync.security.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.BatchSize;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.attackoncodes.worksync.global.Auditable;
import com.attackoncodes.worksync.global.constraints.UserConstraints;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "users", indexes = { @Index(name = "index_user_email", columnList = "email") })
public class User extends Auditable implements UserDetails {

	@Id
	@ToString.Include
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ToString.Include
	@Column(name = "nickname", length = UserConstraints.NICKNAME_MAX_LENGTH, unique = true, nullable = false)
	private String nickname;

	@ToString.Include
	@Column(name = "email", length = UserConstraints.EMAIL_MAX_LENGTH, unique = true, nullable = false)
	private String email;

	@ToString.Include
	@Column(name = "cpf", length = 14, unique = true, nullable = true)
	private String cpf;
	
	@Column(name = "password", unique = false, updatable = true, nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role roles;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 20)
	private List<Token> tokens;

	@Column(name = "account_non_locked", nullable = false)
	private boolean accountNonLocked = true;

	@Column(name = "account_enabled", nullable = false)
	private boolean enabled = true;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.getAuthorities();
	}

	@JsonIgnore
	@Override
	public @Nullable String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User))
			return false;
		return id != null && id.equals(((User) o).id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
