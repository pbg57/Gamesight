package org.gamesight.model;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.data.util.Streamable;

public class Profiles implements Streamable<Profile> {

	private final Streamable<Profile> streamable;

	public Profiles(Streamable<Profile> streamable) {
		this.streamable = streamable;
	}

	@Override
	public Iterator<Profile> iterator() {
		return streamable.iterator();
	}


	Predicate<Profile> NO_STREET = (p) -> p.getStreet() == null;
	Predicate<Profile> NO_ZIP = (p) -> p.getZip() == 0;
	Predicate<Profile> NO_CITY = (p) -> p.getCity() == null;
	Predicate<Profile> NO_STATE = (p) -> p.getState() == null;
	Predicate<Profile> NO_DOB = (p) -> p.getDob() == null;

	public List<Profile> getIncompleteProfiles() {
		List<Profile> profiles =
				streamable.stream().filter(NO_CITY.
						or(NO_STATE).
						or(NO_ZIP).
						or(NO_STREET).
						or(NO_DOB)).collect(Collectors.toList());
		return profiles;
	}


}
