package org.gamesight;

import java.time.LocalDate;
import java.time.Month;

import org.gamesight.model.Game;
import org.gamesight.model.Player;
import org.gamesight.model.Profile;
import org.gamesight.model.User;
import org.gamesight.repository.GameRepository;
import org.gamesight.repository.PlayerRepository;
import org.gamesight.repository.ProfileRepository;
import org.gamesight.repository.UserRepository;

import org.springframework.beans.factory.InitializingBean;
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

	InitializingBeanGamesight(PlayerRepository playerRepository, GameRepository gameRepository,
			UserRepository userRepository, ProfileRepository profileRepository) {
		this.gameRepository = gameRepository;
		this.playerRepository = playerRepository;
		this.userRepository = userRepository;
		this.profileRepository = profileRepository;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
//
		User user1 = new User();
		Profile profile1 = new Profile("6620 Fairways Drive", "Longmont", "CO", 80503, LocalDate.of(1957, Month.NOVEMBER, 29));
		user1.setProfile(profile1);
		profileRepository.save(profile1);
		userRepository.save(user1);
//
//
//
//		Player player = new Player("Dylan B.Griffin");
//		Game game = new Game("WordleCheat", "Online");
//		Game game2 = new Game("JeopardyCheat", "Streaming");
//		player.getGames().add(game);
//		player.getGames().add(game2);
//		player.setUser(user1);
//		game.getPlayers().add(player);
//		game2.getPlayers().add(player);
//		playerRepository.save(player);

//		Player player2 = new Player("Jon O Griffin");
//		Profile profile2 = new Profile("6620 Fairways Drive", "Longmont", "CO", 80503, LocalDate.of(1957, Month.NOVEMBER, 29));
//		User user2 = new User();
//		user2.setProfile(profile2);
//		profile2.setUser(user2);
//		player2.setUser(user2);
//		player2.getGames().add(game);
//		player2.getGames().add(game2);
//		game.getPlayers().add(player2);
//		game2.getPlayers().add(player);
//		userRepository.save(user2);
//		profileRepository.save(profile2);
//		playerRepository.save(player2);

	}
}
