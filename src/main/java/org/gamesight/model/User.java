package org.gamesight.model;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users",
		uniqueConstraints=
		@UniqueConstraint(columnNames={"emailAddress"}))
public class User implements Serializable {

	/*
	A User within the Gamesight application can be an Administrator or a Player.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	LocalDateTime createDate;

	@Email
	String emailAddress;

	@JsonBackReference
	// Note: eager fetch required to have fully up-to-date User object when fetched from userRepository
//	@OneToOne(fetch = FetchType.EAGER, cascade =  CascadeType.ALL, optional = false)
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "profile_id", nullable = false)
	public Profile profile;

	public User() {

		this.createDate = LocalDateTime.now();
	}


	@Override
	public String toString() {
		return "User{" +
				"createDate=" + createDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) +
				'}';
	}

	public LocalDateTime getCreateDate() {

		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {

		this.createDate = createDate;
	}

	public Profile getProfile() {

		return profile;
	}

	public void setProfile(Profile profile) {

		this.profile = profile;
	}

	public Long getId() {

		return id;
	}
	public void setId(Long id) {

		this.id =  id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return id.equals(user.id) && createDate.equals(user.createDate) && profile.equals(user.profile);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, createDate, profile);
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
