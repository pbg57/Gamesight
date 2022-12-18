package org.gamesight.dto;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Id;


public class ProfileDto {
/*
	The ProfileDto is used to transfer business attributes to/from the client/server.
	These attributes are related to Profile model and UI User processing. The attributes
	contained in this POJO should correspond to related processing of them in a
	specific portion of the UI. As the UI design changes, more (or fewer) attributes
	may need to be added here.
	 */

	private String street;
	private String city;
	private String state;
	private int zip;
	private LocalDate dob;
	@Id
	private Long profileId;

	public ProfileDto() {}
	public ProfileDto(Long profileId, String city, String street, String state,int zip, LocalDate dob) {
		this.profileId = profileId;
		this.city = city;
		this.state = state;
		this.street = street;
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
		return profileId;
	}

	public void setId(Long id) {
		this.profileId = id;
	}

	@Override
	public String toString() {
		return "ProfileDto{" +
				"street='" + street + '\'' +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", zip=" + zip +
				", dob=" + dob +
				", profileId=" + profileId +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProfileDto that = (ProfileDto) o;
		return zip == that.zip && Objects.equals(street, that.street) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(dob, that.dob) && Objects.equals(profileId, that.profileId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(street, city, state, zip, dob, profileId);
	}
}
