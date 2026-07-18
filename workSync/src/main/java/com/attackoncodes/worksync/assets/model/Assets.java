package com.attackoncodes.worksync.assets.model;

import java.util.UUID;

import com.attackoncodes.worksync.global.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "assets", indexes = {})
public class Assets extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "object_key", nullable = false, unique = true)
	private String objectKey;

	@Column(name = "object_url", nullable = false, unique = true)
	private String objectUrl;

	@Column(name = "content_type", nullable = false, unique = false)
	private String contentType;

	@Column(name = "size", nullable = false, unique = false)
	private Long size;
	
}
