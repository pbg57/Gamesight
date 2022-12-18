package org.gamesight.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.gamesight.model.Profile;
import org.gamesight.dto.Profiles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
	/*
	The ProfileRepository for the Profile entity.
 	*/

	Optional<Profile> findById(Long id);

	// Derived query methods:
	long countByCity(String cityName);
	long countByState(String stateName);

	List<Profile> findByDob(LocalDate localDate);

	// Query creation methods:
	List<Profile> findByCityAndState(String city, String state);

	List<Profile> findByStreetOrderByZipAsc(String street);

	// Limited query results:
	Slice<Profile> findTop3ByState(String state, Pageable pageable);

	List<Profile> findLast1ByZip(int zip, Sort sort);

	// Define Custom Streamable Wrapper types:
	Profiles findAllByIdNotNull();

	// Note: cannot return pageable objects into streamable Profiles
	//Profiles findAll(Page pageable);

	// Note: cannot override with conflicting return type.
	// Will use the findAllByIdNotNull method as a workaround
//	Page<ProfileDto> findAll(Pageable pageable);
	Page<Profile> findAllByIdNotNull(Pageable pageable);







}