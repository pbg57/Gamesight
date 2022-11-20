package org.gamesight.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gamesight.exception.PatchFieldUnsupportedException;
import org.gamesight.exception.ResourceNotFoundException;
import org.gamesight.model.Profile;
import org.gamesight.repository.ProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	List<Profile> findAllProfiles() {

		return Optional.of(profileRepository.findAll())
				.orElseThrow(() -> new ResourceNotFoundException("finaAllProfiles failed"));
	}

	// TODO: Add @Valid support for Profile request param
		/*
		Save a new Profile.
		 */
	@PostMapping(value = "/api/v1/mgmt/profile")
	//return 201 instead of 200 to signify resource was created
	@ResponseStatus(HttpStatus.CREATED)
	Profile createProfile(@RequestBody Profile profile) {

		return Optional.of(profileRepository.save(profile))
				.orElseThrow(() -> new ResourceNotFoundException("createProfile failed"));
	}

	/*
	Get Profile by Id.
	 */
	@GetMapping("/api/v1/mgmt/profile/{id}")
	Optional<Profile> getProfile(@PathVariable Long id) {
		return Optional.ofNullable(profileRepository.findById(id)    // Return found object else throw exception
				.orElseThrow(() -> new ResourceNotFoundException(id)));
	}

	/*
	 Update the entire Profile via a PUT request
	 */
	@PutMapping("/api/v1/mgmt/profile/{id}")
	Optional<Profile> updateProfile(@RequestBody Profile profileUpdate, @PathVariable Long id) {

		return Optional.of(profileRepository.findById(id)
				.map(x -> {
					x.setStreet(profileUpdate.getStreet());
					x.setCity(profileUpdate.getCity());
					x.setState(profileUpdate.getState());
					x.setZip(profileUpdate.getZip());
					x.setDob(profileUpdate.getDob());
					return profileRepository.save(x);
				})
				.orElseGet(() -> {
					throw new ResourceNotFoundException(id);
				}));
	}

	/*
	 Patch a given field, State, in the Profile
	 */
	@PatchMapping("/api/v1/mgmt/profile/{id}")
	Optional<Profile> patchProfile(@RequestBody Map<String, String> patch, @PathVariable Long id) {

		return Optional.of(profileRepository.findById(id)
				.map(x -> {

					// TODO: iterate on map keySet and Switch on requested fields
					String state = patch.get("state");
					if (!StringUtils.isEmpty(state)) {
						// Update the requested field(s) on the current Profile and persist/patch
						// those fields.
						x.setState(state);
						return profileRepository.save(x);
					}
					else {
						throw new PatchFieldUnsupportedException(patch.keySet());
					}
				})
				.orElseGet(() -> {
					throw new ResourceNotFoundException(id);
				}));
	}

	@DeleteMapping("/api/v1/mgmt/profile/{id}")
	void deleteProfile(@PathVariable Long id) {
		profileRepository.deleteById(id);
	}

}
