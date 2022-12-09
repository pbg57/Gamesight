package org.gamesight.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name= "profiles")
public class Profile implements Serializable {

	/*
	A Profile contains misc. user information managed by the Administrator role. Both
	the Player and Administrator have Profiles via their User entity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 100)
	private String street;
	@Size(max = 100)
	private String city;
	@Size(max = 100)
	private String state;
	private int zip;
	//@Temporal(TemporalType.DATE)
	private LocalDate dob;
	/*
		Profile has a one-to-one relationship with a User.
	 */
	@JsonManagedReference
	@OneToOne(fetch = FetchType.LAZY,
			cascade =  CascadeType.ALL,
			mappedBy = "profile")
	private User user;


	public Profile() {}

	public Profile (String street, String city, String state, int zip, LocalDate dob) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.dob = dob;
	}


	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public Long getId() {
		return id;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Profile profile = (Profile) o;
		return zip == profile.zip && Objects.equals(street, profile.street) && Objects.equals(city, profile.city) && Objects.equals(state, profile.state) && Objects.equals(dob, profile.dob);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, street, city, state, zip, dob);
	}

	@Override
	public String toString() {
		return "Profile{" +
				"id=" + id +
				", street='" + street + '\'' +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", zip=" + zip +
				", dob=" + dob +
				", user=" + user +
				'}';
	}
}
