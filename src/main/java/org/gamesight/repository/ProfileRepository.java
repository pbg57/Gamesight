package org.gamesight.repository;

import java.util.Optional;

import org.gamesight.model.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
	/*
	The ProfileRepository for the Profile entity.
 	*/
	Optional<Profile> findById(Long id);
}