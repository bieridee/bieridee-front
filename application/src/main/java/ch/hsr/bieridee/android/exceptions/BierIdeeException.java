package ch.hsr.bieridee.android.exceptions;

public class BierIdeeException extends RuntimeException {
	public BierIdeeException(String message) {
		super(message);
	}

	public BierIdeeException(String message, Throwable throwable) {
		super(message, throwable);
	}
}