package org.gamesight.controller;

import java.io.Serializable;
import java.util.List;

import org.gamesight.dto.ProfileDto;
import org.gamesight.model.Profile;

import org.springframework.data.domain.Pageable;

public  class FindAllWrapper implements Serializable {
	/*
	Wrapper class for returning paged and non-paged Lists of Profile.
	This wrapper deals with the issue of not being able to transmit (deserialize and reconstruct) a
	Page object on the Rest client-side, during pageable requests. Instead, enough info is
	sent across the wire in this response object to construct the Page object on the client.
	Note:  serialization issues prevented Generic impl of this class.
	 */
	private int pageableStart;
	private int pageableEnd;
	private List<Profile> listWrapper;

	FindAllWrapper() {}

	FindAllWrapper(Pageable pageable,List<Profile> profileList) {
		this.listWrapper = profileList;
		this.pageableStart = (int)pageable.getOffset();
		this.pageableEnd = ((pageableStart + pageable.getPageSize()) >
				profileList.size()) ? profileList.size() :
				pageableStart + pageable.getPageSize();
	}

	public List<Profile> getProfileList() {
		return listWrapper;
	}

	public void setProfileList(List<Profile> listWrapper) {
		this.listWrapper = listWrapper;
	}

	public int getPageableStart() {
		return pageableStart;
	}

	public int getPageableEnd() {
		return pageableEnd;
	}
}
