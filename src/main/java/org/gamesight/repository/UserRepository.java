package org.gamesight.repository;

import java.util.List;
import java.util.Optional;

import org.gamesight.model.Profile;
import org.gamesight.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/*
	The UserRepository for the User entity.
	 */
	Optional<User> findById(Long id);

	// Test for uniqueness of a given email address
	List<User> findByEmailAddress(String emailAddress);

}
