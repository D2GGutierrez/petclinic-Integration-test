package com.tecsup.petclinic.exceptions;

/**
 *
 * Thrown when a Visit record does not exist
 *
 */
public class VisitNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public VisitNotFoundException(String message) {
		super(message);
	}

}
