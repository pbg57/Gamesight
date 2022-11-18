package org.gamesight;

//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gamesight.model.Game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GamesightApplication {

	/*
	Main class for the Gamesight Application.
	 */
	private static final Logger logger = LogManager.getLogger(GamesightApplication.class);


	// start everything
	public static void main(String[] args) {
		SpringApplication.run(GamesightApplication.class, args);
	}

}


