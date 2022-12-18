package org.gamesight.exception;

public class DuplicateResourceException extends RuntimeException{
	public DuplicateResourceException(String emailAddr) {
		super("Resource  [" + emailAddr + "] already exists");
	}
}
