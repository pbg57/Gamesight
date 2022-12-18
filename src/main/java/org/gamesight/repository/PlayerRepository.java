package org.gamesight.repository;

import java.util.Optional;

import org.gamesight.model.Player;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface PlayerRepository extends JpaRepository<Player, Long> {

	// TODO: Add use of PlayerDao repo interface.

	/*
	The PlayerRepository for the Player entity.
	 */
	Page<Player> findById(Long id, Pageable pageable);
//	Optional<Player> findByIdAndGameId(Long id, Long game_Id);
}
