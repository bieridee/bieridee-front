package ch.hsr.bieridee.android.exceptions;

import android.app.Activity;

public class BierIdeeException extends RuntimeException {
	Activity current;
	
	public BierIdeeException(String message) {
		super(message);
	}

	public BierIdeeException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}