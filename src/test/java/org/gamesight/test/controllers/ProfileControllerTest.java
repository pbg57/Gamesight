package org.gamesight.test.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;

import org.gamesight.model.Profile;
//import org.junit.Test;

//import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
//import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
//import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
	This class tests the Gamesight microservices APIs for User and Profile objects.
 */

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProfileControllerTest {

	/*
	Test class for all Restful APIs supported via the ProfileController class.
	 */


	// Bind the above RANDOM_PORT
	@LocalServerPort
	private int port;

	/*
	TestRestTemplate is not an extension of RestTemplate, but rather an alternative that
	simplifies integration testing and facilitates authentication during tests. It helps
	in customization of Apache HTTP client, but also it can be used as a wrapper of
	RestTemplate.
	TestRestTemplate can be used to send http request in our Spring Boot integration
	tests. Such tests are usually executed with Spring boot run as a local server in a
	 random port @LocalServerPort. So just need to create the request in integration
	 tests and send it like a clients of your servers.
	 */
	@Autowired
	private TestRestTemplate restTemplate;

	private final String street = "100 Controller Drive";
	private final String city = "Springfield";
	private final String state = "Missouri";
	private final String stateUpdate = "Pennsylvania";
	private final int zip = 10020;
	private final LocalDate dob = LocalDate.of(2000, Month.NOVEMBER, 29);
	private final Profile testProfile= new Profile(street, city, state, zip,dob);



	@Test
	public void createAndReadProfile() throws Exception {
		/*
		Create new Profile via Gamesight REST API:
		 */
		HttpEntity<Profile> profileHttpEntity = new HttpEntity<>(testProfile);

		ResponseEntity<Profile> postResponse = restTemplate.
				postForEntity(new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString(), profileHttpEntity, Profile.class);
		// Confirm requested fields exist:
		assertEquals(profileHttpEntity.getBody().getState(), postResponse.getBody().getState());

		/*
		 Use the generated PK for Get Profile test
		 */
		Long profileID = postResponse.getBody().getId();

		String url = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/"+profileID).toString();
		ResponseEntity<Profile> response = restTemplate.getForEntity(url , Profile.class);
		// Confirm expected fields exist:
		assertEquals(testProfile.getStreet(), response.getBody().getStreet());
	}


	// TODO - Update and Delete support
}
