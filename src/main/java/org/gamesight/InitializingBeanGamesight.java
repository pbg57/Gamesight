
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
		logger.debug("Hello from Log4j 2 - num : {}", () -> "InitializingBeanGamesight");

//
		//  Initial test data loaded during application start up.
//		User user1 = new User();
//		Profile profile1 = new Profile("6620 Fairways Drive", "Longmont", "CO", 80503, LocalDate.of(1957, Month.NOVEMBER, 29));
//		user1.setProfile(profile1);
//		profileRepository.save(profile1);
//		userRepository.save(user1);

		Profile profile;
		for (int i=0; i<9; i++) {
			// Note: repeated repo.save(profile) operations on same object are an 'update' action, not a 'create' action, of course.
			profile = new Profile("2200 Fairways Drive", "Longmont", "CO", 80503+i, LocalDate.of(1957, Month.NOVEMBER, 29));
			profileRepository.save(profile);
		}

		int pageNo = 0;
		int pageSize =5;
		String sortBy = "zip";
		String sortDir = "ASC";

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

// create Pageable instance
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		Page<Profile> profilePage = profileRepository.findAll(pageable);
		logger.debug("Sorted Profile first zip: {} ", () -> profilePage.getContent().get(0).getZip());
		logPageInfo(profilePage);
		long pagedTotalElements = profilePage.getTotalElements();


		if (profilePage.hasNext()) {
			Pageable pageable2 = profilePage.nextPageable();
			Page<Profile> profilePageNext = profileRepository.findAll(pageable2);
			logPageInfo(profilePageNext);
		}

		List<Profile> profiles = profileRepository.findAll();
		long findAllTotalElements = profiles.size();

		logger.debug("Paged Profile size {}, Non-paged size {}", () -> pagedTotalElements, ()-> findAllTotalElements);



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

	public static void logPageInfo(Page<Profile> profilePage) {
		int number = profilePage.getNumber();
		int numberOfElements = profilePage.getNumberOfElements();
		int size = profilePage.getSize();
		long totalElements = profilePage.getTotalElements();
		int totalPages = profilePage.getTotalPages();
		boolean hasNextPage = profilePage.hasNext();
		logger.debug("Page info - page number {}, numberOfElements: {}, size: {}, "
						+ "totalElements: {}, totalPages: {}, hasNextPage: {}",
				number, numberOfElements, size, totalElements, totalPages, hasNextPage);

		List<Profile> listOfProfiles = profilePage.getContent();

		for(Profile p : listOfProfiles) {
			logger.debug("Profile details: {}", () -> p.toString());
		}
	}
}
