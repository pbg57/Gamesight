package org.gamesight.repository;

import java.util.Optional;

import org.gamesight.model.Player;
import org.gamesight.model.Profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
	/*
	The ProfileRepository for the Profile entity.
 	*/
	Page<Profile> findById(Long id, Pageable pageable);
	Optional<Profile> findById(Long id);
}