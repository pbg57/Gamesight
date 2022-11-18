package org.gamesight.test.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gamesight.model.Profile;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
	// Must use special processing to test Patch operations due to
	// TestRestTemplate limitations
	private RestTemplate patchRestTemplate;

	private final String street = "100 Controller Drive";

	private final String city = "Springfield";
	private final String cityPatch = "Pittsburgh";


	private final String state = "Missouri";

	private final String stateUpdate = "Pennsylvania";
	private final String statePatch = "Arkansas";

	private final int zip = 10020;

	private final LocalDate dob = LocalDate.of(2000, Month.NOVEMBER, 29);

	private final Profile testProfile = new Profile(street, city, state, zip, dob);

	private final Profile testPutProfile = new Profile(street, city, stateUpdate, zip, dob);
	private final Profile testPatchProfile = new Profile();

	private Logger logger = LoggerFactory.getLogger(ProfileControllerTest.class);


	@Test
	public void crudProfile() throws Exception {
		logger.info(()->"Executing crudProfile Tests...");

		/*
		Test Create new Profile via Gamesight REST API:
		 */
		HttpEntity<Profile> profileHttpEntity = new HttpEntity<>(testProfile);

		String postURL = new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString();
		ResponseEntity<Profile> postResponse = null;
		try {
			postResponse = restTemplate.
					postForEntity(postURL, profileHttpEntity, Profile.class);
			// Confirm the persisted profile matches the original:
			assertEquals(testProfile, postResponse.getBody());
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed to create Profile via Post request");
		}


		/*
		 Test Get Profile using generated PK from Create test
		 */
		long profileID = (postResponse.getBody() != null) ? postResponse.getBody().getId() : -1L;

		String getUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileID).toString();
		try {
			ResponseEntity<Profile> getResponse = restTemplate.getForEntity(getUrl, Profile.class);
			// Confirm expected fields exist:
			assertEquals(testProfile, getResponse.getBody());
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed fetch Profile via Get request");
		}


		/*
		Test Put Profile operation
		 */
		String putUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileID).toString();
		try {
			restTemplate.put(putUrl, testPutProfile);
			// Confirm expected fields exist:
			ResponseEntity<Profile> getResponse = restTemplate.getForEntity(getUrl, Profile.class);
			assertEquals(testPutProfile, getResponse.getBody());
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed update ofProfile via Put request");
		}

		/*
		Test Patch Profile operation
		 */
		String patchUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileID).toString();
		Map<String, String>	patchMap = new HashMap<>();
		patchMap.put("state", statePatch);
		// Create JSON Object - update a single field in the existing Profile
		JSONObject updateBody = new JSONObject();
		updateBody.put("state", statePatch);

		// Note: TestRestTemplate does not support the Patch operation. Use HttpClient for patching.
		patchRestTemplate = restTemplate.getRestTemplate();
		HttpClient httpClient = HttpClientBuilder.create().build();
		patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

		try {
			// Use patchRestTemplate to make call with PATCH method
			ResponseEntity<Profile> patchResponse =
					patchRestTemplate.exchange(patchUrl, HttpMethod.PATCH, getPatchHeaders(updateBody.toString()), Profile.class);
				assertEquals(statePatch, patchResponse.getBody().getState());
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed update of Profile via Patch request");
		}

		/*
		Test retrieval of multiple Profiles using the Pageable wrapper.
		 */

		// First, create a few more Profiles:
		int profileNum = 10;
		for (int i=0; i<profileNum; i++) {
			try {
				postResponse = restTemplate.
						postForEntity(postURL, profileHttpEntity, Profile.class);
				// Confirm the persisted profile matches the original:
				assertEquals(testProfile, postResponse.getBody());
			}
			catch (RestClientException rce) {
//				Assertions.fail("Failed to create Profile via Post request");
			}
		}
		// Retrieve all existing Profiles:
		String getAllUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString();
		try {
			ResponseEntity<Profile> getResponse = restTemplate.getForEntity(getAllUrl, Profile.class);
			Profile profile = getResponse.getBody();
			// Confirm expected fields exist:
//			assertEquals(testProfile, getResponse.getBody());
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed fetch Profile via Get request");
		}

	}

	public HttpEntity<String> getPatchHeaders(String jsonPostBody) {
		List<MediaType> acceptTypes = new ArrayList<>();
		acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);

		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		reqHeaders.setAccept(acceptTypes);

		return new HttpEntity<String>(jsonPostBody, reqHeaders);
	}

	// TODO - Update and Delete support
}
