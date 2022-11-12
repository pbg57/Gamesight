package org.gamesight.controller;

import java.util.Optional;

import org.gamesight.exception.ResourceNotFoundException;
import org.gamesight.model.Profile;
import org.gamesight.model.User;
import org.gamesight.repository.ProfileRepository;
import org.gamesight.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
	public class ProfileController {


		/*
		The ProfileController provides the REST CRUD services for the Profile entity.
 		*/
		@Autowired
		private ProfileRepository profileRepository;

		/*
		Get all existing Profile records.
		 */
		@GetMapping("/api/v1/mgmt/profile")
		Page<Profile> findAllProfiles(Pageable pageable) {

			return profileRepository.findAll(pageable);
		}

		// TODO: Add @Valid support for Profile request param

		/*
		Save a new Profile.
		 */
		@PostMapping(value="/api/v1/mgmt/profile")
		//return 201 instead of 200
		@ResponseStatus(HttpStatus.CREATED)
		Profile createProfile(@RequestBody Profile profile) {

			return profileRepository.save(profile);
		}

		/*
		Find Profile by Id.
		 */
		@GetMapping("/api/v1/mgmt/profile/{id}")
		Optional<Profile> findProfile(@PathVariable Long id) {
			return Optional.ofNullable(profileRepository.findById(id)    // Return found object else throw exception
					.orElseThrow(() -> new ResourceNotFoundException(id)));
		}

		@DeleteMapping("/api/v1/mgmt/profile/{id}")
		void deleteUser(@PathVariable Long id) {
			profileRepository.deleteById(id);
		}

	}
