package org.gamesight.repository;

import java.util.Optional;

import org.gamesight.model.Game;
import org.gamesight.model.Player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
	// TODO: Add use of GameDao repo interface.

	/*
	The GameRepository for the Game entity.
	 */
}