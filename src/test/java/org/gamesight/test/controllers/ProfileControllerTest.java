package org.gamesight.test.controllers;

import java.net.MalformedURLException;
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
//	private final String cityPatch = "Pittsburgh";


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

		Profile createdProfile = createProfile(profileHttpEntity);
		if (createdProfile != null) {
			// Confirm the persisted profile matches the original:
			assertEquals(testProfile, createdProfile);
		} //  else failed assertion already made

		/*
		 Test Get Profile using generated PK from Create test
		 */

		long profileID = (createdProfile != null) ? createdProfile.getId() : -1L;
		Profile profile = getProfileById(profileID);

		if (profile != null) {
			// Confirm expected fields exist:
			assertEquals(testProfile, profile);
		}	// else failed assertion already made


		/*
		Test Put Profile operation
		 */
		Profile putProfile = putProfileById(profileID, testPutProfile);

		if (putProfile != null) {
			// Confirm expected fields exist:
			assertEquals(testPutProfile, putProfile);
		}	// else failed assertion already made


		/*
		Test Patch Profile operation
		 */
		Map<String, String>	patchMap = new HashMap<>();
		patchMap.put("state", statePatch);
		// Create JSON Object - update a single test field in the existing Profile
		JSONObject updateBody = new JSONObject();
		updateBody.put("state", statePatch);

		Profile patchedProfile = patchProfileById(profileID, updateBody);

		if (patchedProfile != null) {
			// Confirm requested field was updated:
			assertEquals(statePatch, patchedProfile.getState());
		}	// else failed assertion already made

		/*
		Test Delete Profile.
		 */
		boolean deleteSuccess = deleteProfileById(profileID);

		if (deleteSuccess) {
			// TODO: Confirm Profile has been removed by trying to retrieve it.
		}	// else failed assertion already made

	}

//	@Test
//	public void findAllProfile() throws Exception {
//		logger.info(() -> "Executing crudProfile Tests...");
///*
//		Test retrieval of multiple Profiles using the Pageable wrapper.
//		 */
//		String postURL = new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString();
//		ResponseEntity<Profile> postResponse = null;
//
//		// First, create a few more Profiles:
//		int profileNum = 10;
//		for (int i=0; i<profileNum; i++) {
//			try {
//				postResponse = restTemplate.
//						postForEntity(postURL, profileHttpEntity, Profile.class);
//				// Confirm the persisted profile matches the original:
//				assertEquals(testProfile, postResponse.getBody());
//			}
//			catch (RestClientException rce) {
////				Assertions.fail("Failed to create Profile via Post request");
//			}
//		}
//		// Retrieve all existing Profiles:
//		String getAllUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString();
//		try {
//			ResponseEntity<Profile[]> getResponse = restTemplate.getForEntity(getAllUrl, Profile[].class);
//			Profile[] profiles = getResponse.getBody();
//			// Confirm expected fields exist:
//			assertEquals(profileNum, profiles.length);
//		}
//		catch (RestClientException rce) {
//			Assertions.fail("Failed fetch Profile via Get request");
//		}
//
//	}
	public HttpEntity<String> getPatchHeaders(String jsonPostBody) {
		List<MediaType> acceptTypes = new ArrayList<>();
		acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);

		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		reqHeaders.setAccept(acceptTypes);

		return new HttpEntity<String>(jsonPostBody, reqHeaders);
	}

	public void removeAllProfiles() {

	}

	public Profile createProfile(HttpEntity<Profile> profileHttpEntity) {
		try {
			String postURL = new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString();
			ResponseEntity<Profile> postResponse = restTemplate.
					postForEntity(postURL, profileHttpEntity, Profile.class);
			// Caller tests persisted profile matches the original?
			return postResponse.getBody();
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed to create Profile via Post request");
			logger.error(()->"CreateProfile caught exception: " + rce.toString());
		}
		catch (MalformedURLException mue) {
			Assertions.fail("CreateProfile has malformed URL");
			logger.error(() -> "CreateProfile has malformed URL: " + mue.toString());
		}
		return null;
	}

	public Profile getProfileById(long profileId) {
		/*
		 Test Get Profile using generated PK from Create test
		 */
		try {
			String getUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileId).toString();
			ResponseEntity<Profile> getResponse = restTemplate.getForEntity(getUrl, Profile.class);
			// Return Profile. Caller to validate contents.
			return (getResponse.getBody());
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed fetch Profile via Get request for id: " + profileId);
			logger.error(() -> "getProfileById caught exception: " + rce.toString());
		}
		catch (MalformedURLException mue) {
			Assertions.fail("getProfileById has malformed URL");
			logger.error(() -> "getProfileById has malformed URL: " + mue.toString());
		}
		return null;
	}
	public Profile putProfileById(long profileId, Profile putProfile) {
		/*
		 Test Put Profile using generated PK from Create test
		 */
		try {
			String putUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileId).toString();
			restTemplate.put(putUrl, putProfile);
			ResponseEntity<Profile> putResponse = restTemplate.getForEntity(putUrl, Profile.class);
			// Caller to confirm expected fields exist:
			return (putResponse.getBody());
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed update of Profile via Put request for id: " + profileId);
			logger.error(() -> "putProfileById caught exception: " + rce.toString());
		}
		catch (MalformedURLException mue) {
			Assertions.fail("putProfileById has malformed URL");
			logger.error(() -> "putProfileById has malformed URL: " + mue.toString());
		}
		return null;
	}

	public Profile patchProfileById(long profileId, JSONObject jsonUpdateBody ) {


		// Note: TestRestTemplate does not support the Patch operation. Use HttpClient for patching.
		patchRestTemplate = restTemplate.getRestTemplate();
		HttpClient httpClient = HttpClientBuilder.create().build();
		patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

		// Note: the jsonUpdateBody contains the name/value Map with requested patch fields.
		try {
			String patchUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileId).toString();
			// Use patchRestTemplate to make call with PATCH method
			ResponseEntity<Profile> patchResponse =
					patchRestTemplate.exchange(patchUrl, HttpMethod.PATCH, getPatchHeaders(jsonUpdateBody.toString()), Profile.class);
			// Caller to confirm patch results.
			return patchResponse.getBody();
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed patch of Profile via request for id/body: " + profileId + " " + jsonUpdateBody.toString());
			logger.error(() -> "patchProfileById caught exception: " + rce.toString());
		}
		catch (MalformedURLException mue) {
			Assertions.fail("patchProfileById has malformed URL");
			logger.error(() -> "patchProfileById has malformed URL: " + mue.toString());
		}
		return null;
	}
	public boolean deleteProfileById( long profileId) {
		try {
			String deleteUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileId).toString();
			restTemplate.delete(deleteUrl);
			// Caller to confirm Profile has been removed:
			return true;
		}
		catch (RestClientException rce) {
			Assertions.fail("deleteProfileById caught exception for profile id : " + profileId);
			logger.error(() -> "deleteProfileById caught exception: " + rce.toString());
		}
		catch (MalformedURLException mue) {
			Assertions.fail("deleteProfileById has malformed URL");
			logger.error(() -> "deleteProfileById has malformed URL: " + mue.toString());
		}
		return false;
	}
}
