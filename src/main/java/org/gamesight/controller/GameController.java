package org.gamesight.controller;

import org.gamesight.exception.ResourceNotFoundException;
import org.gamesight.model.Game;
import org.gamesight.model.Player;
import org.gamesight.repository.GameRepository;
import org.gamesight.repository.PlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

	/*
	The GameController provides the REST CRUD services for the Game entity.
	 */

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private PlayerRepository playerRepository;


	// TODO: Add @Valid support for player request param

	// Create a new Game for an existing Player
	@PostMapping(value = "/api/v1/mgmt/player/{playerId}/game")
	//return 201 instead of 200
	@ResponseStatus(HttpStatus.CREATED)
	Game createGame(@PathVariable(value = "playerId") Long playerId,
			@RequestBody Game game) {
		return playerRepository.findById(playerId).map(player -> {
			game.getPlayers().add(player);
			return gameRepository.save(game);
		}).orElseThrow(() -> new ResourceNotFoundException("PlayerId " + playerId + " not found."));
	};

	// TODO: Add remaining CRUD services
}
