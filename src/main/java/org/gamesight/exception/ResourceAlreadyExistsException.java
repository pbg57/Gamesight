package org.gamesight.exception;

public class ResourceAlreadyExistsException extends Exception {
	/*
	Checked exception to be handled by Gamesight application
	 */
	public ResourceAlreadyExistsException(String res,Long id) {
		super("Resource ["+ res + "] with id [" + id + "] already exists : ");
	}

	public ResourceAlreadyExistsException(String s) {
		super(s);
	}
}
