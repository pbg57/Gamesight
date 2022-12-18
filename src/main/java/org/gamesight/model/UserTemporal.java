package org.gamesight.model;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;


@MappedSuperclass
public class UserTemporal implements Serializable {
	// TODO: Investigate use of UserTemporal by base Entity classes
	/*
	A UserTemporal superclass holds  time-related data describing a player.
	The @MappedSuperclass annotation allows any super class data declared to be
	persisted along with the subclass entity.
	 */
	public UserTemporal() {
	}
}
