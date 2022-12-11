package org.gamesight.controller;

import java.util.Optional;

import org.gamesight.exception.ResourceNotFoundException;
import org.gamesight.model.Profile;
import org.gamesight.model.User;
import org.gamesight.repository.ProfileRepository;
import org.gamesight.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

	@RestController
	public class UserController {

		/*
		The UserController provides the REST CRUD services for the User entity.
 		*/
		private UserRepository userRepository;
		private ProfileRepository profileRepository;

		UserController(UserRepository userRepository, ProfileRepository profileRepository) {
			this.userRepository = userRepository;
			this.profileRepository = profileRepository;
		}
		/*
		Get all existing User records.
		 */
		@GetMapping("/api/v1/mgmt/user")
		Page<User> findAll(Pageable pageable) {

			return userRepository.findAll(pageable);
		}

		// TODO: Add @Valid support for user request param

		/*
		Save a new User.
		 */
		@PostMapping(value="/api/v1/mgmt/user")
		//return 201 instead of 200
		@ResponseStatus(HttpStatus.CREATED)
		User newUser(@RequestBody User user) {

			// temp
			Profile profile = new Profile();
			profileRepository.save(profile);
			user.setProfile(profile);
			return userRepository.save(user);
		}

		/*
		Find User by Id.
		 */
		@GetMapping("/api/v1/mgmt/user/{id}")
		Optional<User> findOne(@PathVariable Long id) {
			Optional<User> user =  Optional.ofNullable(userRepository.findById(id)    // Return found object else throw exception
					.orElseThrow(() -> new ResourceNotFoundException(id)));
			return user;
		}


		@DeleteMapping("/api/v1/mgmt/user/{id}")
		void deleteUser(@PathVariable Long id) {
			userRepository.deleteById(id);
		}

	}
