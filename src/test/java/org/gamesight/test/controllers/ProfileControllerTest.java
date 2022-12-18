package org.gamesight.test.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.gamesight.controller.FindAllWrapper;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	private final Profile testProfile = new Profile(null, street, city, state, zip, dob);

	private final Profile testPutProfile = new Profile(null, street, city, stateUpdate, zip, dob);

	private final Profile testPatchProfile = new Profile();

	private Logger logger = LoggerFactory.getLogger(ProfileControllerTest.class);


	@Test
	public void crudProfile() throws Exception {
		logger.info(() -> "Executing crudProfile Tests...");

		/*
		Test Create new Profile via Gamesight REST API:
		 */
		HttpEntity<Profile> profileHttpEntity = new HttpEntity<>(testProfile);

		// Test the Rest interface
		Profile createdProfile = createProfile(profileHttpEntity);
		if (createdProfile != null) {
			// Confirm the persisted profile matches the original:
			assertEquals(testProfile, createdProfile);
		} //  else failed assertion already made

		/*
		 Test Get Profile using generated PK from Create test
		 */

		long profileID = (createdProfile != null) ? createdProfile.getId() : -1L;
		// Test the Rest interface
		Profile profile = getProfileById(profileID);

		if (profile != null) {
			// Confirm expected fields exist:
			assertEquals(testProfile, profile);
		}    // else failed assertion already made


		/*
		Test Put Profile operation
		 */

		// Test the Rest interface
		Profile putProfile = putProfileById(profileID, testPutProfile);

		if (putProfile != null) {
			// Confirm expected fields exist:
			assertEquals(testPutProfile, putProfile);
		}    // else failed assertion already made


		/*
		Test Patch Profile operation
		 */
//		Map<String, String>	patchMap = new HashMap<>();
//		patchMap.put("state", statePatch);
		// Create JSON Object - update a single test field in the existing Profile
		JSONObject updateBody = new JSONObject();
		updateBody.put("state", statePatch);

		// Test the Rest interface
		Profile patchedProfile = patchProfileById(profileID, updateBody);

		if (patchedProfile != null) {
			// Confirm requested field was updated:
			assertEquals(statePatch, patchedProfile.getState());
		}    // else failed assertion already made

		/*
		Test Delete Profile.
		 */
		// Test the Rest interface
		boolean deleteSuccess = deleteProfileById(profileID);

		if (deleteSuccess) {
			// TODO: Confirm Profile has been removed by trying to retrieve it.
		}    // else failed assertion already made

	}

	public void findAllProfiles() throws Exception {
		logger.info(() -> "Executing findAllProfile Tests...");
		/*
		Test retrieval of multiple Profiles.
		 */

		// Clear out any existing profiles and create a known number of new Profiles:
		removeAllProfiles();
		int numToCreate = 12;
		createTestProfiles(numToCreate);

		// Retrieve all existing Profiles, without paging.
		String getAllUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString();
		try {
			ResponseEntity<FindAllWrapper> getResponse = restTemplate.getForEntity(getAllUrl, FindAllWrapper.class);
			List<Profile> profileList = getResponse.getBody().getProfileList();
			assertEquals(numToCreate, profileList.size(), "FindAllProfiles returned wrong number of results. ");
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed fetch Profile via Get request");
			logger.error(() -> "findAllPagedProfiles caught exception: " + rce.toString());
		}
	}


	@Test
	public void findAllPagedProfiles() throws Exception {
		logger.info(() -> "Executing findAllPagedProfiles Tests...");

		/*
		Test retrieval of multiple Profiles using the Pageable wrapper.
		 */

		// Clear out any existing profiles and create a known number of new Profiles:
		removeAllProfiles();
		int numToCreate = 8;		// choose a size creating just 2 pages of results
		createTestProfiles(numToCreate);

		// Retrieve all existing Profiles, with paging.
		String getAllUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile").toString();
		try {
			ResponseEntity<FindAllWrapper> getResponse = restTemplate.getForEntity(getAllUrl, FindAllWrapper.class);
			List<Profile> profileList = getResponse.getBody().getProfileList();
			assertEquals(numToCreate, profileList.size(), "findAllPagedProfiles returned wrong number of results. ");
			int highestUnsortedZipNumber = profileList.get(profileList.size() - 1).getZip();

			int pageNo = 0;        // Start on page zero, not page one !!!!
			int pageSize = 5;		// Choose a page size creating just two pages of results
			String sortBy = "zip";
			String sortDir = "DESC";    // Profiles were just added with an ascending zip, so test with descending order.

			LinkedMultiValueMap<String, String> linkedMultiValueMap = buildPagingTestMap(pageNo, pageSize, sortBy, sortDir);

			/*
			Paging done over the wire will be done by sending paging attributes via
			Request parameters, since Request body objects (like Pageable) aren't normally used in Get
			Requests, so it will be constructed in the Controller.
			 */

			Sort sort = (sortDir == null || sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())) ?
					Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

			// Construct a Pageable. It shouldn't be sent in the Get Request body, but will be
			// used to re-construct the Page object post-request.
			Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
			UriComponents buildUriComponents = buildUriComponents(getAllUrl, linkedMultiValueMap);

			// Test our ProfileController interface:
			ResponseEntity<FindAllWrapper> getPagedResponse =
					restTemplate.exchange(buildUriComponents.toUri(), HttpMethod.GET,
							buildRequestEntity(null), FindAllWrapper.class);

			/*
			 Results are wrapped in an application wrapper which supports results from
			 either the paged findAll or non-paged findAll Profile requests.
			 */
			FindAllWrapper findAllWrapper = getPagedResponse.getBody();
			assert findAllWrapper != null;
			List<Profile> pagedProfileList = findAllWrapper.getProfileList();

			// Test that the descending sort request worked:
			// Check that the descending sort on zip has returned the expected zip code
			int firstZipReturned = profileList.get(0).getZip();

			assertEquals(highestUnsortedZipNumber, pagedProfileList.get(0).getZip(), "Pageable sort order failed.");

			// Test that the expected number of Profiles were returned on the first page:
			long pagedTotalElements = findAllWrapper.getProfileList().size();
			assertEquals(pageSize, pagedTotalElements,  "Number of persisted findAll() Profiles differs with query by findAll(pageable)");

			// Reconstruct the Page object using returned params and the last pageable object
			Page<Profile> profilePage;
			profilePage = new PageImpl<>(pagedProfileList.subList(findAllWrapper.getPageableStart(),
					findAllWrapper.getPageableEnd()), pageable, pagedProfileList.size());

			assertEquals(pageSize, profilePage.getTotalElements(),
					"findAllPagedProfiles Profiles returned wrong number of results. ");

			// Test for existence of expected second Page of results
			// Note: profilePage.hasNext() doesn't seem to work. The usefulness of
			// reconstructing the Page<Profile> based on the PageImpl needs more research.
			// Manually tracking your paging and making needed repeated paged calls is
			// an alternative.
			// Note: use of hasNext() works in local tests (see RepositoryPagingAndSortingTests).

			if (pageable.isPaged()) {	// Need more test cases for isPaged().
				Pageable pageable2 = profilePage.nextPageable();
				// Test our ProfileController interface for the second page of results.
				// Increment just the page number to be sent, requesting the second page.
				LinkedMultiValueMap<String, String> linkedMultiValueMap2 =
						buildPagingTestMap(pageNo+1, pageSize, sortBy, sortDir);
				UriComponents buildUriComponents2 = buildUriComponents(getAllUrl, linkedMultiValueMap2);

				ResponseEntity<FindAllWrapper> getSecondPagedResponse =
						restTemplate.exchange(buildUriComponents2.toUri(), HttpMethod.GET,
								buildRequestEntity(null), FindAllWrapper.class);

				FindAllWrapper findAllWrapper2 = getSecondPagedResponse.getBody();
				assert findAllWrapper2 != null;
				List<Profile> pagedProfileList2 = findAllWrapper2.getProfileList();
				assertEquals(pagedProfileList2.size(), numToCreate-pageSize,
						"Number of persisted findAll() Profiles differs with query by findAll(pageable)");
			}
			else {
				assertTrue(profilePage.hasNext(),
						"Pageable findAllPagedProfiles(pageable) failed to return expected next page");
			}

//			logger.debug("Paged Profile size {}, Non-paged size {}", () -> pagedTotalElements, () -> findAllTotalElements);
		}
		catch (RestClientException rce) {
			Assertions.fail("Failed fetch Profile via Get request");
			logger.error(() -> "findAllPagedProfiles caught exception: " + rce.toString());
		}
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
			logger.error(() -> "CreateProfile caught exception: " + rce.toString());
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

	public Profile patchProfileById(long profileId, JSONObject jsonUpdateBody) {


		// Note: TestRestTemplate does not support the Patch operation. Use HttpClient for patching.
		patchRestTemplate = restTemplate.getRestTemplate();
		HttpClient httpClient = HttpClientBuilder.create().build();
		patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

		// Note: the jsonUpdateBody contains the name/value Map with requested patch fields.
		try {
			String patchUrl = new URL("http://localhost:" + port + "/api/v1/mgmt/profile/" + profileId).toString();
			// Use patchRestTemplate to make call with PATCH method
			ResponseEntity<Profile> patchResponse =
					patchRestTemplate.exchange(patchUrl, HttpMethod.PATCH, buildRequestEntity(jsonUpdateBody.toString()), Profile.class);
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

	public boolean deleteProfileById(long profileId) {
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

	public HttpEntity<String> buildRequestEntity(String jsonPostBody) {
		List<MediaType> acceptTypes = new ArrayList<>();
		acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);

		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		reqHeaders.setAccept(acceptTypes);

		return new HttpEntity<String>(jsonPostBody, reqHeaders);
	}

	public UriComponents buildUriComponents(String requestUrl,
			LinkedMultiValueMap<String, String> requestParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(requestUrl)
				.queryParams(requestParams);

		// encode() is to ensure that characters like {, }, are preserved and not encoded.
		return builder.build().encode();
//		ResponseEntity<Object> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET,entity, String.class);
//		return responseEntity;
	}

	public void createTestProfiles(int numToCreate) {
		// Create several Profiles. Modify the zip in each profile for sorting test.
		Profile profile;
		int lastZipNumber = 0;

		for (int i = 0; i < numToCreate; i++) {
			lastZipNumber = 10020 + i;
			profile = new Profile(null, "2200 Fairways Drive", "Longmont", "CO", lastZipNumber, LocalDate.of(1957, Month.NOVEMBER, 29));
			HttpEntity<Profile> profileHttpEntity = new HttpEntity<>(profile);
			createProfile(profileHttpEntity);
		}
	}

	public LinkedMultiValueMap<String, String> buildPagingTestMap(int pageNo, int pageSize, String sortBy,String sortDir)
			{
			/*
			Paging done over the wire will be done by sending paging attributes via
			Request parameters, since Request body objects aren't normally used in Get
			Requests.
			 */
		LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
		linkedMultiValueMap.put("pageSize", Arrays.asList(Integer.toString(pageSize)));
		linkedMultiValueMap.put("pageNum", Arrays.asList(Integer.toString(pageNo)));
		linkedMultiValueMap.put("sortBy", Arrays.asList(sortBy));
		linkedMultiValueMap.put("sortDir", Arrays.asList(sortDir));
		return linkedMultiValueMap;
	}
}
