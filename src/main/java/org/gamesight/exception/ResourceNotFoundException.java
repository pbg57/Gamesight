package org.gamesight.exception;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(Long id) {
		super("Resource id [" + id + "] not found : ");
	}

	public ResourceNotFoundException(String s) {
		super(s);
	}
}
