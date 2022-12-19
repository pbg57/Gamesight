package org.gamesight.test.standalone;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gamesight.model.Profile;
import org.gamesight.dto.Profiles;
import org.gamesight.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO - register Junit5 extension
//@RunWith(MyJunit5Extension.class)
@SpringBootTest
public class RepositoryPagingAndSortingTests {


	@Autowired
	ProfileRepository profileRepository;

	private static final Logger logger = LogManager.getLogger(RepositoryPagingAndSortingTests.class);


	@DisplayName("Test Profile entity paging operations via Java APIs")
	@Test
	void profilePaging() {

		// Prepare for test by removing existing profiles.
		removeAllProfiles();

		// Create multiple profiles for paging tests. The zip code field will differ in each new profile to allow sort testing.
		int numToCreate = 10;
		int lastZipNumber = createTestProfiles(numToCreate);

		// Make sure we have the expected number of profiles:
		List<Profile> profiles = profileRepository.findAll();
		long findAllTotalElements = profiles.size();
		assertEquals(findAllTotalElements, numToCreate, "Wrong number of Profiles exist in DB at start of paging test.");

		// Setup paging and sorting params.
		int pageNo = 0;		// Start on page zero, not page one !!!!
		int pageSize = 5;
		String sortBy = "zip";
		String sortDir = "DESC";	// Profiles were just added with an ascending zip, so test with descending order.

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		Page<Profile> profilePage = profileRepository.findAllByIdNotNull(pageable);
//		Page<ProfileDto> profilePageDto = profileRepository.findAllByIdNotNull(pageable);


		int firstZipReturned = profilePage.getContent().get(0).getZip();
		//profilePage.getContent().set(0, new Profile());
		logger.debug("Sorted Profile first zip: {} ", () -> firstZipReturned);
		// Test that the descending sort on zip has returned the expected zip code
		assertEquals(lastZipNumber, firstZipReturned, "Pageable sort order failed.");

		logPageInfo(profilePage);
		long pagedTotalElements = profilePage.getTotalElements();

		// Test for existence of second Page of results:
		if (profilePage.hasNext()) {
			Pageable pageable2 = profilePage.nextPageable();
			Page<Profile> profilePageNext = profileRepository.findAllByIdNotNull(pageable2);
			logPageInfo(profilePageNext);
		} else {
			assertTrue(profilePage.hasNext(), "Pageable findAll(pageable) failed to return expected second page");
		}

		logger.debug("Paged Profile size {}, Non-paged size {}", () -> pagedTotalElements, () -> findAllTotalElements);
		assertEquals(pagedTotalElements, findAllTotalElements, "Number of persisted findAll() Profiles differs with query by findAll(pageable)");

		// Clean up post-test by removing profiles.
		removeAllProfiles();
	}

	@DisplayName("Test Profile entity derived-query operations via Java APIs")
	@Test
	void profileDerivedQuery() {
		// Prepare for test by removing existing profiles.
		removeAllProfiles();

		// Create multiple profiles for derived query tests.
		int numToCreate = 10;
		createTestProfiles(numToCreate);

		// Test Profile repo derived query
		String city = "Longmont";
		long cityCount = profileRepository.countByCity(city);
		assertEquals(cityCount, numToCreate, "Wrong number of Profiles exist in DB for query-by-city test.");

		LocalDate dob = LocalDate.of(1957, Month.NOVEMBER, 29);
		List<Profile> dobProfileList = profileRepository.findByDob(dob);
		assertEquals(dobProfileList.size(), numToCreate, "Wrong number of Profiles exist in DB for query-by-dob test.");

		// Clean up post-test by removing profiles.
		removeAllProfiles();
	}


	@DisplayName("Test Profile entity derived-query operations via Java APIs")
	@Test
	void profileQueryCreation() {
		// Prepare for test by removing existing profiles.
		removeAllProfiles();

		// Create multiple profiles for derived query tests.
		int numToCreate = 10;
		int lastZipNumber = createTestProfiles(numToCreate);

		// Test Profile repo derived query
		String city = "Longmont";
		String state = "CO";
		List<Profile> cityStateProfileList = profileRepository.findByCityAndState(city,state);
		assertEquals(cityStateProfileList.size(), numToCreate, "Wrong number of Profiles exist in DB for query-by-city test.");

		String street = "2200 Fairways Drive";
		List<Profile> streetZipAscProfiles = profileRepository.findByStreetOrderByZipAsc(street);
		int lastZipFoundAsc = streetZipAscProfiles.get(streetZipAscProfiles.size()-1).getZip();
		assertEquals(lastZipFoundAsc, lastZipNumber, "Wrong zip number in Profiles exist in DB for query creation test.");

		// Clean up post-test by removing profiles.
		removeAllProfiles();
	}

	@DisplayName("Test Profile entity limited query result operations via Java APIs")
	@Test
	void profileLimitedQuery() {
		// Prepare for test by removing existing profiles.
		removeAllProfiles();

		// Create multiple profiles for derived query tests.
		int numToCreate = 15;
		int lastZipNumber = createTestProfiles(numToCreate);

		// Test Profile repo derived query
		String state = "CO";
		Slice<Profile> top3ByState = profileRepository.findTop3ByState( state,Pageable.unpaged());
		assertEquals(top3ByState.getSize(), 3, "Wrong number of Profiles exist in DB for limited-query test.");

		Sort sort = Sort.by("zip").ascending();
		List<Profile> lastZipAsc = profileRepository.findLast1ByZip(lastZipNumber, sort);
		assertEquals(lastZipAsc.get(lastZipAsc.size()-1).getZip(), lastZipNumber, "Wrong zip number in Profiles exist in DB for limited-query-sort test.");

		// Clean up post-test by removing profiles.
		removeAllProfiles();
	}
	@DisplayName("Test Profile entity Custom Streamable wrapper result operations via Java APIs")
	@Test
	void profileFindIncomplete() {
		// Prepare for test by removing existing profiles.
		removeAllProfiles();

		// Create multiple profiles for derived query tests.
		int numToCreate = 15;

		// Test Profile repo for no incomplete profiles
		Profiles profiles  = profileRepository.findAllByIdNotNull();
		List<Profile> incompleteProfileList = profiles.getIncompleteProfiles();
		assertEquals(incompleteProfileList.size(), 0, "Wrong  number of Profiles exist in DB for find-incomplete profiles test.");

		// Clean up post-test by removing profiles.
		removeAllProfiles();
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

		for (Profile p : listOfProfiles) {
			logger.debug("Profile details: {}", p::toString);
		}
	}

	public void removeAllProfiles() {
		List<Profile> profileList = profileRepository.findAll();
		for (Profile p : profileList) {
			profileRepository.deleteById(p.getId());
		}
		profileList = profileRepository.findAll();
		assertEquals(0, profileList.size(), "Unable to remove all Profiles to begin testing.");
	}

	public int createTestProfiles(int numToCreate) {
		Profile profile;
		int lastZipNumber = 0;
		for (int i = 0; i < numToCreate; i++) {
			// Note: repeated repo.save(profile) operations on same object are an 'update' action, not a 'create' action, of course.
			lastZipNumber = 80503 + i;
			profile = new Profile(null, "2200 Fairways Drive", "Longmont", "CO", lastZipNumber, LocalDate.of(1957, Month.NOVEMBER, 29));
			profile = profileRepository.save(profile);
			assertNotNull(profile, "Unable to create all Profiles to begin testing.");
		}
		return lastZipNumber;
	}
}
