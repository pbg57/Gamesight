package org.gamesight.test.standalone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gamesight.dao.ProfileDao;
import org.gamesight.dao.UserDao;
import org.gamesight.dto.ProfileDto;
import org.gamesight.dto.UserDto;
import org.gamesight.exception.ResourceAlreadyExistsException;
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

	String emailAddress = "John.Doe@gmail.com";

	LocalDate dob = LocalDate.of(2000, Month.NOVEMBER, 29);

	// User test data:
	String userName = "Test User Name";

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserDao userDao;

	@Autowired
	ProfileDao profileDao;

	@PersistenceContext
	EntityManager entityManager;

	private static final Logger logger = LogManager.getLogger(UserAndProfileCrudTests.class);

	@DisplayName("Test Profile entity CRUD operations via Java APIs")
	@Test
	void crudProfileAndUsers() throws ResourceAlreadyExistsException {

		ProfileDto profileDto = new ProfileDto(null, city, street, state, zip, dob);
		UserDto userDtoWithAttr = new UserDto(null, null, emailAddress,street,city,state,zip,dob);


		Assert.notNull(profileDto, "Unable to construct Profile class.");

		/*
		Basic CRUD operations
		 */
		assertEquals(profileDto.getStreet(), street, "Profile street value is incorrect");
		assertEquals(profileDto.getCity(), city, "Profile city value is incorrect");
		assertEquals(profileDto.getState(), state, "Profile state value is incorrect");
		assertEquals(profileDto.getZip(), zip, "Profile zip value is incorrect");
		assertEquals(profileDto.getDob(), dob, "Profile dob value is incorrect");

		/*
		 Database persistence operations
		 */

		// Must persist transient profile instance prior to persisting it within a User
		profileDto = profileDao.createProfile(profileDto);
		Optional<ProfileDto> optionalPersistedProfileDto = profileDao.findById(profileDto.getId());
		assertTrue(optionalPersistedProfileDto.isPresent(), "Optional return value does not contain Profile for id [" + profileDto.getId());

		// Check Profile values returned from database.
		ProfileDto persistedProfileDto = optionalPersistedProfileDto.get();
		assertEquals(profileDto, persistedProfileDto, "Profile retrieved from profileRepository doesn't match");

		// Test the cascaded fetch of the Profile for an existing User/Profile pair:
		UserDto userDtoCascadeFetch = new UserDto();
		String newCity = "Valmont";
		userDtoCascadeFetch.setCity(newCity);
		userDtoCascadeFetch = userDao.createUser(userDtoCascadeFetch);
		ProfileDto profileDtoCascadeFetch = (profileDao.findById(userDtoCascadeFetch.getProfileId())).get();
		assertEquals(profileDtoCascadeFetch.getId(), userDtoCascadeFetch.getProfileId(), "Embedded Profile in User has wrong ID");
		UserDto userDtoCascadeFetch2 = new UserDto();
		userDtoCascadeFetch2.setId((userDtoCascadeFetch.getId()));
		userDtoCascadeFetch2 = (userDao.findById(userDtoCascadeFetch.getId())).get();
		assertEquals(userDtoCascadeFetch2.getProfileId(), userDtoCascadeFetch2.getProfileId(), "Get profile with embedded Profile failed");

		// test to see if the embedded Profile contains the city used during createUser()?
		assertEquals(newCity, userDtoCascadeFetch2.getCity(), "Fetch of UserDto missing city update");

		profileDto.setStreet("My Way");
		ProfileDto profileDto1 = null;
		try {
			profileDto1 = profileDao.createProfile(profileDto);
			logger.debug("Negative test: Create Profile with an PK != null  succeeded but should have failed: ");
		}
		catch (ResourceAlreadyExistsException e) {

			// Expected exception
		}


		// Test simple field update:
		ProfileDto profileDtoTest = new ProfileDto();
		profileDtoTest.setCity("Miami");
		profileDtoTest.setZip(12345);
		profileDtoTest =  profileDao.createProfile(profileDtoTest);
		ProfileDto profileDtoTest2 = (profileDao.findById(profileDtoTest.getId()).get());
		assertEquals(profileDtoTest.getCity(), profileDtoTest2.getCity(), "Error: city names do not match!");

		ProfileDto  newProfileDto = new ProfileDto();
		newProfileDto.setCity("Panama City");
		newProfileDto.setId(profileDtoTest2.getId());
		newProfileDto = profileDao.updateProfile(newProfileDto);
		assertEquals("Panama City", newProfileDto.getCity(), "Error: city names do not match!");
		assertTrue( 12345 != newProfileDto.getZip(), " Retrieved zips do not match");

		// Test user creation with a DTO attributes in place and check they were persisted?
		userDtoWithAttr = userDao.createUser(userDtoWithAttr);
		UserDto userDtoAttr = (userDao.findById(userDtoWithAttr.getId())).get();
		assertEquals(emailAddress, userDtoAttr.getEmailAddress(), "Fetched UserDto missing Dto attribute");
		ProfileDto profileDtoAttr = (profileDao.findById(userDtoAttr.getProfileId())).get();
		assertEquals(street, profileDtoAttr.getStreet(), "Fetched ProfileDto missing Dto attribute");

		// Create User will create a new Profile and associated it with the User.
		UserDto userDto = new UserDto();
		String emailAddress = "foobar@home.com";
		String badEmailAddress = "foobar@home.@com";

		userDto.setEmailAddress(emailAddress);
		userDto = userDao.createUser(userDto);

		// Check User values returned from database.
		Optional<UserDto> optionalPersistedUserDto = userDao.findById(userDto.getId());
		assertTrue(optionalPersistedUserDto.isPresent(), "Optional return value does not contain User for id [" + userDto.getId());
		UserDto persistedUserDto = optionalPersistedUserDto.get();

		// Check User values returned from database.
		assertEquals(userDto, persistedUserDto, "User retrieved from UserRepository doesn't match");
		assertNotNull(persistedUserDto.getProfileId(), "Retrieved User object from database with null profile");

		// Retrieve the profile created during createUser, and confirm Dto has correct profile id
		ProfileDto createdProfileDto = (profileDao.findById(persistedUserDto.getProfileId())).get();
		assertEquals(createdProfileDto.getId(), persistedUserDto.getProfileId(), "Retrieved Profile persisted in User doesn't match");

		// Test update, persist, and read operations:
		LocalDateTime localDateNow = LocalDateTime.now();
		persistedUserDto.setCreateDate(localDateNow);
		assertEquals(localDateNow, persistedUserDto.getCreateDate(), "Update of User createdate failed");
		persistedUserDto = userDao.updateUser(persistedUserDto);
		assertEquals(localDateNow, persistedUserDto.getCreateDate(), "Update of User createdate failed after UserRepository.save() operation");
		assertEquals(emailAddress,  persistedUserDto.getEmailAddress(), "Update of User createdate failed after UserRepository.save() operation");

		// Create a new userDto that only changes a single attribute, issue a updateUser(),
		// and confirm the existing entity was merged correctly?
		UserDto userDtoUpdate = new UserDto();
		userDtoUpdate.setEmailAddress(badEmailAddress);
		userDtoUpdate.setCreateDate(persistedUserDto.getCreateDate());
		userDtoUpdate.setId(persistedUserDto.getId());
		userDtoUpdate = userDao.updateUser(userDtoUpdate);
		assertEquals(localDateNow, userDtoUpdate.getCreateDate(), "Update of User createdate failed after UserRepository.save() operation");
		assertEquals(badEmailAddress,  userDtoUpdate.getEmailAddress(), "Update of User emailaddress failed after UserRepository.save() operation");



		// Update a Profile attribute's value in the User DTO, and see if it gets propagated
		// to the attached User Profile after updating just the User.
		persistedUserDto.setState(stateUpdate);
		persistedUserDto = userDao.updateUser(persistedUserDto);

		// The preceeding userDao.updateUser() call should indirectly update the User's Profile as well.
		// The assertion tests that the UserDto correctly gets updated during the Profile's update.
		assertEquals(stateUpdate, persistedUserDto.getState(), "Update of User state failed after update operation");

		// Must refresh user...
		optionalPersistedUserDto = userDao.findById(userDto.getId());
		assertTrue(optionalPersistedUserDto.isPresent(), "Optional return value does not contain User for id [" + userDto.getId());
		persistedUserDto = optionalPersistedUserDto.get();

		assertEquals(stateUpdate, persistedUserDto.getState(), "Update of User state failed after UserRepository.save() operation");

	}

}