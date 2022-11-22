package org.gamesight.test.standalone;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gamesight.model.Profile;
import org.gamesight.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


	@DisplayName("Test Profile entity CRUD operations via Java APIs")
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
		Page<Profile> profilePage = profileRepository.findAll(pageable);
		int firstZipReturned = profilePage.getContent().get(0).getZip();
		logger.debug("Sorted Profile first zip: {} ", () -> firstZipReturned);
		// Test that the descending sort on zip has returned the expected zip code
		assertEquals(lastZipNumber, firstZipReturned, "Pageable sort order failed.");

		logPageInfo(profilePage);
		long pagedTotalElements = profilePage.getTotalElements();

		// Test for existence of second Page of results:
		if (profilePage.hasNext()) {
			Pageable pageable2 = profilePage.nextPageable();
			Page<Profile> profilePageNext = profileRepository.findAll(pageable2);
			logPageInfo(profilePageNext);
		} else {
			assertTrue(profilePage.hasNext(), "Pageable findAll(pageable) failed to return expected second page");
		}

		logger.debug("Paged Profile size {}, Non-paged size {}", () -> pagedTotalElements, () -> findAllTotalElements);
		assertEquals(pagedTotalElements, findAllTotalElements, "Number of persisted findAll() Profiles differs with query by findAll(pageable)");

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
			profile = new Profile("2200 Fairways Drive", "Longmont", "CO", lastZipNumber, LocalDate.of(1957, Month.NOVEMBER, 29));
			profile = profileRepository.save(profile);
			assertNotNull(profile, "Unable to create all Profiles to begin testing.");
		}
		return lastZipNumber;
	}
}
