
package org.gamesight;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gamesight.model.Game;
import org.gamesight.model.Player;
import org.gamesight.model.Profile;
import org.gamesight.model.User;
import org.gamesight.repository.GameRepository;
import org.gamesight.repository.PlayerRepository;
import org.gamesight.repository.ProfileRepository;
import org.gamesight.repository.UserRepository;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class InitializingBeanGamesight implements InitializingBean {

	/*
	Placeholder class for application initialization actions.
	 */
	PlayerRepository playerRepository;

	GameRepository gameRepository;

	ProfileRepository profileRepository;

	UserRepository userRepository;

	private static final Logger logger = LogManager.getLogger(InitializingBeanGamesight.class);


	InitializingBeanGamesight(PlayerRepository playerRepository, GameRepository gameRepository,
			UserRepository userRepository, ProfileRepository profileRepository) {
		this.gameRepository = gameRepository;
		this.playerRepository = playerRepository;
		this.userRepository = userRepository;
		this.profileRepository = profileRepository;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Add any application startup processing here.
	}
}
