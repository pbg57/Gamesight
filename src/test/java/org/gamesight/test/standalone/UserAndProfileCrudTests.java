package org.gamesight.test.standalone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import org.gamesight.model.Profile;
import org.gamesight.model.User;
import org.gamesight.repository.ProfileRepository;
import org.gamesight.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@ExtendWith(MyJunitExtension.class)
@SpringBootTest
public class UserAndProfileCrudTests {

	/*
	Test class for User and Profile entities operations.
	 */


	// User Profile data:
	String street = "100 Maple Drive";
	String city = "Springfield";
	String state = "Missouri";
	String stateUpdate = "Pennsylvania";

	int zip = 10020;
	LocalDate dob = LocalDate.of(2000, Month.NOVEMBER, 29);

	// User test data:
	String userName = "Test User Name";
	@Autowired
	ProfileRepository profileRepository;
	@Autowired
	UserRepository userRepository;


	@DisplayName("Test Profile entity CRUD operations via Java APIs")
	@Test
	void crudProfileAndUsers() {

		Profile profile= new Profile(street, city, state, zip,dob);

		Assert.notNull(profile, "Unable to construct Profile class.");

		/*
		Basic CRUD operations
		 */
		assertEquals(profile.getStreet(), street, "Profile street value is incorrect");
		assertEquals(profile.getCity(), city, "Profile city value is incorrect");
		assertEquals(profile.getState(), state, "Profile state value is incorrect");
		assertEquals(profile.getZip(), zip, "Profile zip value is incorrect");
		assertEquals(profile.getDob(), dob, "Profile dob value is incorrect");

		/*
		 Database persistence operations
		 */

		// Must persist transient profile instance prior to persisting it within a User
		profileRepository.save(profile);
		Optional<Profile> optionalPersistedProfile = profileRepository.findById(profile.getId());
		assertTrue(optionalPersistedProfile.isPresent(), "Optional return value does not contain Profile for id ["+ profile.getId());

		// Check Profile values returned from database.
		Profile persistedProfile = optionalPersistedProfile.get();
		assertEquals(profile, persistedProfile, "Profile retrieved from profileRepository doesn't match");

		// Cannot persist User without an associated Profile
		User user = new User();
		user.setProfile(persistedProfile);
		String emailAddress = "foobar@home.com";
		String badEmailAddress = "foobar@home.@com";

		user.setEmailAddress(badEmailAddress);
		userRepository.save(user);

		// Check User values returned from database.
		Optional<User> optionalPersistedUser = userRepository.findById(user.getId());
		assertTrue(optionalPersistedUser.isPresent(), "Optional return value does not contain User for id ["+ user.getId());
		User persistedUser = optionalPersistedUser.get();

		// Check User values returned from database.
		assertEquals(user, persistedUser, "User retrieved from UserRepository doesn't match");
		assertNotNull(persistedUser.getProfile(), "Retrieved User object from database with null profile");
		assertEquals(persistedProfile, persistedUser.getProfile(), "Retrieved Profile persisted in User doesn't match");

		// Test update, persist, and read operations:
		LocalDateTime localDateNow = LocalDateTime.now();
		persistedUser.setCreateDate(localDateNow);
		assertEquals(localDateNow, persistedUser.getCreateDate(), "Update of User createdate failed");
		userRepository.save(persistedUser);
		assertEquals(localDateNow, persistedUser.getCreateDate(), "Update of User createdate failed after UserRepository.save() operation");

		persistedUser.getProfile().setState(stateUpdate);
		userRepository.save(persistedUser);
		// Note: the following assertion succeeds, but is only checking the in-memory status of the user profile. The DB has not been updated.
		assertEquals(stateUpdate, persistedUser.getProfile().getState(), "Update of User state failed after update operation");

		// Now, update the profile in the DB and retest...
		profileRepository.save(persistedUser.getProfile());
		assertEquals(stateUpdate, persistedUser.getProfile().getState(), "Update of User state failed after update operation");

		// Must refresh user...
		optionalPersistedUser = userRepository.findById(user.getId());
		assertTrue(optionalPersistedUser.isPresent(), "Optional return value does not contain User for id ["+ user.getId());
		persistedUser = optionalPersistedUser.get();

		assertEquals(stateUpdate, persistedUser.getProfile().getState(), "Update of User state failed after UserRepository.save() operation");

	}

}