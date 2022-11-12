package org.gamesight.repository;

import java.util.Optional;

import org.gamesight.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/*
	The UserRepository for the User entity.
	 */
	Optional<User> findById(Long id);
}
