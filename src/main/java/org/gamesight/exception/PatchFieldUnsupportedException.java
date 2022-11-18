package org.gamesight.exception;

import java.util.Set;

public class PatchFieldUnsupportedException extends RuntimeException {

		public PatchFieldUnsupportedException(Set<String> keys) {
			super("Field " + keys.toString() + " update is not allowed.");
		}

	}
