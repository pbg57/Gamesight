package org.gamesight.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.gamesight.exception.ResourceNotFoundException;
import org.gamesight.model.Player;
import org.gamesight.repository.PlayerRepository;

@RestController
public class PlayerController {

	/*
	The PlayerController provides the REST CRUD services for the Player entity.
	 */

	private PlayerRepository playerRepository;

	PlayerController(PlayerRepository playerRepository){
		this.playerRepository = playerRepository;
	}
	/*
	Get all existing Player records.
	 */
	@GetMapping("/api/v1/mgmt/player")
	Page<Player> findAll(Pageable pageable) {
		return playerRepository.findAll(pageable);
	}

	// TODO: Add @Valid support for player request param

	/*
	Save a new Player.
	 */
	@PostMapping(value="/api/v1/mgmt/player")
	//return 201 instead of 200
	@ResponseStatus(HttpStatus.CREATED)
	Player newPlayer(@RequestBody Player player) {

		return playerRepository.save(player);
	}

	/*
	Find Player by Id.
	 */
	@GetMapping("/api/v1/mgmt/player/{id}")
	Player findOne(@PathVariable Long id) {
		return playerRepository.findById(id)	// Return found object else throw exception
				.orElseThrow(() -> new ResourceNotFoundException(id));
	}

	@DeleteMapping("/api/v1/mgmt/player/{id}")
	void deletePlayer(@PathVariable Long id) {
		playerRepository.deleteById(id);
	}

}
