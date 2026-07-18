package com.attackoncodes.worksync.assets.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attackoncodes.worksync.assets.model.Assets;

@Repository
public interface AssetsRepository extends JpaRepository<Assets, UUID> {

	Optional<Assets> findByName(String name);

	boolean existsByName(String name);

}
