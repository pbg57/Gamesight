package org.gamesight.test.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MainControllerTest {

	// Bind the above RANDOM_PORT
	@LocalServerPort
	private int port;

	/*
	Test class for all Restful APIs supported via the MainController class.
	 */
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void getHello() throws Exception {

		ResponseEntity<String> response = restTemplate.
				getForEntity(new URL("http://localhost:" + port + "/").toString(), String.class);
		assertEquals("Gamesight says Hello!", response.getBody());

	}

}
