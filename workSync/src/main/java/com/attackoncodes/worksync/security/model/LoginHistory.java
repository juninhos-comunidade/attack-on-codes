package com.attackoncodes.worksync.security.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
@Table(name = "login_history", indexes = {
		@Index(name = "idx_history_login_time", columnList = "login_time"),
})
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
    private User user;
    
	@Column(name = "login_time",
			unique = false,
			updatable = false,
			nullable = false)
    private ZonedDateTime loginTime;

	@Column(name = "ip_address",
			unique = false,
			updatable = false,
			nullable = false)
    private String ipAddress;
	
}
