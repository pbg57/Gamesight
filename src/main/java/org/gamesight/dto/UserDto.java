package org.gamesight.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.Email;

import org.gamesight.model.Profile;

public class UserDto {
	/*
	The UserDto is used to transfer business attributes to/from the client/server.
	These attributes are related to User model and UI User processing. The attributes
	contained in this POJO should correspond to related processing of them in a
	specific portion of the UI. As the UI design changes, more (or fewer) attributes
	may need to be added here.

	Due to the close user/profile UI relationship, this DTO pulls in Profile attributes as a
	convenience.
	 */

// TODO add validator logic to support Email annotation
	@Email
	private String emailAddress;

//	private Profile profile;
	private String street;
	private String city;
	private String state;
	private int zip;
	private LocalDate dob;
	private Long userId = null;
	private Long profileId = null;
	private LocalDateTime createDate;
	public UserDto() {
	}

	public UserDto(Long userId, Long profileId, String emailAddress, String street,
			String city, String state, int zip, LocalDate dob) {
		this.userId = userId;
		this.profileId = profileId;
		this.emailAddress = emailAddress;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.dob = dob;
	}


	public Long getId() {
		return userId;
	}

	public void setId(Long userId) {
		this.userId = userId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}


	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
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

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "UserDto{" +
				"emailAddress='" + emailAddress + '\'' +
				", street='" + street + '\'' +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", zip=" + zip +
				", dob=" + dob +
				", userId=" + userId +
				", profileId=" + profileId +
				", createDate=" + createDate +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserDto userDto = (UserDto) o;
		return zip == userDto.zip && Objects.equals(emailAddress, userDto.emailAddress) && Objects.equals(street, userDto.street) && Objects.equals(city, userDto.city) && Objects.equals(state, userDto.state) && Objects.equals(dob, userDto.dob) && Objects.equals(userId, userDto.userId) && Objects.equals(profileId, userDto.profileId) && Objects.equals(createDate, userDto.createDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(emailAddress, street, city, state, zip, dob, userId, profileId, createDate);
	}
}
